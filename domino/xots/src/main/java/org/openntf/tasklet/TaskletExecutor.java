/**
 * 
 */
package org.openntf.tasklet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openntf.domino.commons.ILifeCycle;
import org.openntf.domino.commons.IO;
import org.openntf.domino.commons.LifeCycleManager;
import org.openntf.domino.commons.ServiceLocator;

/**
 * A ThreadPoolExecutor for Tasklets runnables. It sets up a shutdown hook for proper termination.
 * 
 * TODO
 * 
 * @author Nathan T. Freeman
 * @author Roland Praml
 */
public class TaskletExecutor extends ThreadPoolExecutor implements ILifeCycle {
	static final Logger log_ = Logger.getLogger(TaskletExecutor.class.getName());

	/** This list contains ALL tasks */
	protected Map<Long, TaskletFutureTask<?>> tasks = new ConcurrentHashMap<Long, TaskletFutureTask<?>>();

	private String executorName_;

	private ITaskletWrapperFactory wrapperFactory_;

	private static final AtomicLong sequencer = new AtomicLong(0L);

	/**
	 * A FutureTask for {@link WrappedCallable}s and {@link WrappedRunnable}s. It is nearly identical with
	 * {@link ScheduledThreadPoolExecutor.ScheduledFutureTask} But ScheduledFutureTask is private, so that we cannot inherit
	 * 
	 * @author Roland Praml, FOCONIS AG
	 */
	public class TaskletFutureTask<RET> extends FutureTask<RET> implements Observer, Comparable<TaskletFutureTask<?>> {

		private final long sequenceNumber = sequencer.incrementAndGet();

		private TaskState state = TaskState.SLEEPING;

		/** the objectState reported from the observable */
		private Object objectState;

		/** the thread that runs this future */
		private volatile Thread runner;

		/** the wrapped callable */
		protected ITaskletWrapper<RET> taskletWrapper;

		/**
		 * Sets the new state of this Thread
		 * 
		 * @param s
		 */
		synchronized void setState(final TaskState s) {
			state = s;
		}

		/**
		 * Returns the State of this task
		 * 
		 * @return the {@link TaskState}
		 */
		public synchronized TaskState getState() {
			return state;
		}

		/**
		 * Returns the ID for this task
		 * 
		 * @return the Id
		 */
		public long getId() {
			return sequenceNumber;
		}

		public TaskletFutureTask(final ITaskletWrapper<RET> wrappedCallable) {
			super(wrappedCallable);
			this.taskletWrapper = wrappedCallable;
			wrappedCallable.setObserver(this);
		}

		/**
		 * log exception
		 */
		@Override
		protected void setException(Throwable t) {
			super.setException(t);
			if (t instanceof ExecutionException) {
				t = ((ExecutionException) t).getCause();
				log_.log(Level.WARNING, "Task '" + getDescription() + "' failed: " + t.toString(), t);
			} else {
				log_.log(Level.SEVERE, "Task '" + getDescription() + "' failed: " + t.toString(), t);
			}
		}

		@Override
		public final void run() {
			this.runner = Thread.currentThread();
			try {
				super.run();
			} finally {
				synchronized (this) {
					this.runner = null;
				}
			}
		}

		/**
		 * Cancels the FutureTask. Tries also to cancel the inner task. If this is a periodic task, it will stop
		 */
		@Override
		public boolean cancel(final boolean mayInterruptIfRunning) {
			taskletWrapper.stop();

			if (super.cancel(mayInterruptIfRunning)) {
				return true;
			}
			if (mayInterruptIfRunning) {
				// unfortunately subsequent calls to cancel will return false.
				synchronized (this) {
					if (runner != null)
						runner.interrupt();
					return true;
				}
			}
			return false;
		}

		public String getDescription() {
			return taskletWrapper.getDescription();
		}

		@Override
		public String toString() {
			// TODO increment Period/Time 
			return sequenceNumber + "State: " + getState() + " Task: " + getDescription() + " objectState: " + objectState;
		}

		@Override
		public void update(final Observable arg0, final Object arg1) {
			objectState = arg1;
		}

		@Override
		public int compareTo(final TaskletFutureTask<?> paramT) {
			return (int) Math.abs(sequenceNumber - paramT.sequenceNumber);
		}

	}

	/**
	 * Returns a privilegedThreadFactory, to run higher privileged operations
	 */
	private static ThreadFactory createThreadFactory() {
		try {
			return Executors.privilegedThreadFactory();
		} catch (Throwable t) {
			log_.log(Level.WARNING,
					"cannot create a privilegedThreadFactory - this is the case if you run as java app or in an unsupported operation: "
							+ t.toString(), t);
			return Executors.defaultThreadFactory();
		}
	}

	/**
	 * Creates a new {@link TaskletExecutor}.
	 * 
	 */
	public TaskletExecutor(final int corePoolSize, final int maxPoolSize, final String executorName,
			final ITaskletWrapperFactory wrapperFactory) {
		super(corePoolSize, maxPoolSize, 30, TimeUnit.SECONDS, new LinkedBlockingQueue(), createThreadFactory());
		executorName_ = executorName;
		wrapperFactory_ = wrapperFactory;
		if (wrapperFactory_ == null)
			throw new NullPointerException("No ITaskletWrapperFactory specified");
		if (executorName_ == null)
			throw new NullPointerException("No ExecutorName specified");
		LifeCycleManager.addLifeCycle(this);
	}

	public TaskletExecutor(final int corePoolSize, final int maxPoolSize, final String executorName) {
		this(corePoolSize, maxPoolSize, executorName, ServiceLocator.findApplicationService(ITaskletWrapperFactory.class));
	}

	@SuppressWarnings("unused")
	private long overflowFree(long paramLong) {
		Delayed localDelayed = (Delayed) super.getQueue().peek();
		if (localDelayed != null) {
			long l = localDelayed.getDelay(TimeUnit.NANOSECONDS);
			if ((l < 0L) && (paramLong - l < 0L))
				paramLong = Long.MAX_VALUE + l;
		}
		return paramLong;
	}

	// --- end duplicate stuff

	/**
	 * Returns a list of all tasks sorted by next execution time or comparator
	 * 
	 * @return a List of tasks
	 */
	public List<TaskletFutureTask<?>> getTasks(final Comparator<TaskletFutureTask<?>> comparator) {
		ArrayList<TaskletFutureTask<?>> ret = new ArrayList<TaskletFutureTask<?>>();

		for (TaskletFutureTask<?> task : tasks.values()) {
			ret.add(task);
		}
		if (comparator == null) {
			Collections.sort(ret);
		} else {
			Collections.sort(ret, comparator);
		}
		return ret;
	}

	/**
	 * Return a Task with the given ID. May return null if the task is no longer in the queue.
	 * 
	 * @param id
	 *            The ID of the task to retrieve.
	 * @return The task for the given ID, or null if the task is no longer in the queue.
	 */
	public TaskletFutureTask<?> getTask(final long id) {
		return tasks.get(id);
	}

	/**
	 * Method is executed before a runnable starts. It mainly sets the Task state and the thread name
	 */
	@Override
	protected void beforeExecute(final Thread thread, final Runnable runnable) {
		super.beforeExecute(thread, runnable);
		if (runnable instanceof TaskletFutureTask)
			((TaskletFutureTask<?>) runnable).setState(TaskState.RUNNING);
		thread.setName(getThreadName(runnable));
	}

	/**
	 * Method is called after execution. It mainly sets the Task state and resets the thread name
	 */
	@Override
	protected void afterExecute(final Runnable runnable, final Throwable error) {
		super.afterExecute(runnable, error);
		if (runnable instanceof TaskletFutureTask) {
			TaskletFutureTask<?> task = (TaskletFutureTask<?>) runnable;
			if (task.isDone()) {
				task.setState(error == null ? TaskState.DONE : TaskState.ERROR);
				tasks.remove(task.getId());
			}
		}
		Thread.currentThread().setName("(IDLE) " + getThreadName(runnable));
	}

	/**
	 * Returns the executor-name of the given runnable
	 */
	private String getThreadName(final Runnable runnable) {
		StringBuilder sb = new StringBuilder(executorName_).append(": ");
		if (runnable instanceof TaskletFutureTask) {
			sb.append(((TaskletFutureTask<?>) runnable).getDescription());
			sb.append(" - ");
			sb.append(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(new Date()));
		} else {
			sb.append('#').append(Thread.currentThread().getId());
		}
		return sb.toString();
	}

	@Override
	public void execute(final Runnable runnable) {
		// TODO Auto-generated method stub
		TaskletFutureTask<?> future = null;
		if (runnable instanceof TaskletFutureTask<?>) {
			future = (TaskletFutureTask<?>) runnable;
			tasks.put(future.getId(), future);
		} else {
			// TODO: Should we allow this?
			ITaskletWrapper<Object> wrapper = wrapperFactory_.createWrapper(runnable, null);
			future = new TaskletFutureTask<Object>(wrapper);
		}
		super.execute(runnable);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
		ITaskletWrapper<T> wrapper = wrapperFactory_.createWrapper(callable);
		return new TaskletFutureTask<T>(wrapper);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T type) {
		ITaskletWrapper<T> wrapper = wrapperFactory_.createWrapper(runnable, type);
		return new TaskletFutureTask<T>(wrapper);
	};

	@Override
	public void shutdown() {
		shutdownNow();
		LifeCycleManager.removeLifeCycle(this);
		try {
			for (int i = 5; i > 0; i--) {
				if (!awaitTermination(10, TimeUnit.SECONDS)) {
					if (i > 0) {
						IO.println("Could not terminate java threads... Still waiting " + (i * 10) + " seconds");
					} else {
						IO.println("Could not terminate java threads... giving up. Server may crash now.");
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startup() {

	}

	@Override
	public int getPriority() {
		return 0;
	}

}
