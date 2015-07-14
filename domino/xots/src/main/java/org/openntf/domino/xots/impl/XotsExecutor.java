/**
 * 
 */
package org.openntf.domino.xots.impl;

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
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openntf.domino.commons.ILifeCycle;
import org.openntf.domino.commons.IO;
import org.openntf.domino.commons.LifeCycleManager;
import org.openntf.domino.commons.ServiceLocator;
import org.openntf.domino.xots.IXotsWrapper;
import org.openntf.domino.xots.IXotsWrapperFactory;
import org.openntf.domino.xots.Scheduler;
import org.openntf.domino.xots.TaskState;
import org.openntf.domino.xots.XotsExecutorService;

/**
 * A ThreadPoolExecutor for Xots runnables. It sets up a shutdown hook for proper termination.
 * 
 * 
 * The <code>XotsExecutor.schedule</code> schedules a Runnable or a Callable for execution. The Runnable/Callable is wrapped in a
 * {@link WrappedRunnable} (i.e. {@link WrappedCallable}) which is responsible for proper Thread setUp/tearDown.<br>
 * 
 * The Wrapped Runnable is wrapped again in a {@link XotsFutureTask} which observes the Runnable and keeps track of some status information.<br>
 * 
 * 
 * <b>This class should not be used directly. Use XotsDaemon.getInstance() instead</b>
 * 
 * @author Nathan T. Freeman
 * @author Roland Praml
 */
public class XotsExecutor extends ScheduledThreadPoolExecutor implements XotsExecutorService, ILifeCycle {
	static final Logger log_ = Logger.getLogger(XotsExecutor.class.getName());

	/** This list contains ALL tasks */
	protected Map<Long, XotsFutureTask<?>> tasks = new ConcurrentHashMap<Long, XotsFutureTask<?>>();

	private String executorName_;

	private IXotsWrapperFactory wrapperFactory_;

	private static final AtomicLong sequencer = new AtomicLong(0L);

	/**
	 * A FutureTask for {@link WrappedCallable}s and {@link WrappedRunnable}s. It is nearly identical with
	 * {@link ScheduledThreadPoolExecutor.ScheduledFutureTask} But ScheduledFutureTask is private, so that we cannot inherit
	 * 
	 * @author Roland Praml, FOCONIS AG
	 */
	public class XotsFutureTask<RET> extends FutureTask<RET> implements Delayed, RunnableScheduledFuture<RET>, Observer {

		/** the scheduler determines when this future will be executed the next time */
		private Scheduler scheduler;

		private final long sequenceNumber = sequencer.incrementAndGet();

		private TaskState state = TaskState.QUEUED;

		/** the objectState reported from the observable */
		private Object objectState;

		/** the thread that runs this future */
		private volatile Thread runner;

		/** the wrapped callable */
		protected IXotsWrapper<RET> xotsWrapper;

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

		public XotsFutureTask(final IXotsWrapper<RET> wrappedCallable, final Scheduler scheduler) {
			super(wrappedCallable);
			this.scheduler = scheduler;
			this.xotsWrapper = wrappedCallable;
			wrappedCallable.setObserver(this);
		}

		@Override
		public boolean isPeriodic() {
			return scheduler.isPeriodic();
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

		private void runPeriodic() {

			scheduler.eventStart();
			boolean success = super.runAndReset();

			if (success && (!isShutdown() || ((getContinueExistingPeriodicTasksAfterShutdownPolicy()) && (!isTerminating())))) {
				// Re-Add the future, if it runs periodically
				scheduler.eventStop();
				getQueue().add(this);
			}
		}

		@Override
		public final void run() {
			this.runner = Thread.currentThread();
			try {
				if (isPeriodic()) {
					runPeriodic();
				} else {
					super.run();
				}
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
			xotsWrapper.stop();

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

		@Override
		public long getDelay(final TimeUnit timeUnit) {
			long delay = scheduler.getNextExecutionTimeInMillis() - System.currentTimeMillis();
			return timeUnit.convert(delay, TimeUnit.MILLISECONDS);
		}

		public long getNextExecutionTimeInMillis() {
			return scheduler.getNextExecutionTimeInMillis();
		}

		@Override
		public int compareTo(final Delayed other) {
			long delta = 0;
			if (other instanceof XotsFutureTask) {
				delta = getNextExecutionTimeInMillis() - ((XotsFutureTask<?>) other).getNextExecutionTimeInMillis();
			} else {
				delta = getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS);
			}

			if (delta < 0L)
				return -1;
			if (delta > 0L)
				return 1;
			//delta == 0 - compare sequence numbers
			if ((other instanceof XotsFutureTask)) {
				if (this.sequenceNumber < ((XotsFutureTask<?>) other).sequenceNumber) {
					return -1;
				}
				return 1;
			}
			return 0;
		}

		public String getDescription() {
			return xotsWrapper.getDescription();
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
	 * Creates a new {@link AbstractXotsExecutor}.
	 * 
	 */
	public XotsExecutor(final int corePoolSize, final String executorName, final IXotsWrapperFactory wrapperFactory) {
		super(corePoolSize, createThreadFactory());
		executorName_ = executorName;
		wrapperFactory_ = wrapperFactory;
		if (wrapperFactory_ == null)
			throw new NullPointerException();
		LifeCycleManager.addLifeCycle(this);
	}

	public XotsExecutor(final int corePoolSize) {
		this(50, "Xots");
	}

	public XotsExecutor(final int corePoolSize, final String execName) {
		super(corePoolSize, createThreadFactory());
		executorName_ = execName;
		wrapperFactory_ = ServiceLocator.findApplicationService(IXotsWrapperFactory.class);
		if (wrapperFactory_ == null)
			throw new NullPointerException();
		LifeCycleManager.addLifeCycle(this);
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
	@Override
	public List<XotsFutureTask<?>> getTasks(final Comparator<XotsFutureTask<?>> comparator) {
		ArrayList<XotsFutureTask<?>> ret = new ArrayList<XotsFutureTask<?>>();

		for (XotsFutureTask<?> task : tasks.values()) {
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
	public XotsFutureTask<?> getTask(final long id) {
		return tasks.get(id);
	}

	/**
	 * Method is executed before a runnable starts. It mainly sets the Task state and the thread name
	 */
	@Override
	protected void beforeExecute(final Thread thread, final Runnable runnable) {
		super.beforeExecute(thread, runnable);
		if (runnable instanceof XotsFutureTask)
			((XotsFutureTask<?>) runnable).setState(TaskState.RUNNING);
		thread.setName(getThreadName(runnable));
	}

	/**
	 * Method is called after execution. It mainly sets the Task state and resets the thread name
	 */
	@Override
	protected void afterExecute(final Runnable runnable, final Throwable error) {
		super.afterExecute(runnable, error);
		if (runnable instanceof XotsFutureTask) {
			XotsFutureTask<?> task = (XotsFutureTask<?>) runnable;
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
		if (runnable instanceof XotsFutureTask) {
			sb.append(((XotsFutureTask<?>) runnable).getDescription());
			sb.append(" - ");
			sb.append(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(new Date()));
		} else {
			sb.append('#').append(Thread.currentThread().getId());
		}
		return sb.toString();
	}

	/**
	 * Maintains startup and queues the given task
	 */
	protected <V> RunnableScheduledFuture<V> queue(final XotsFutureTask<V> future) {
		if (isShutdown()) {
			throw new RejectedExecutionException();
		}
		if (getPoolSize() < getCorePoolSize()) {
			prestartCoreThread();
		}

		tasks.put(future.getId(), future);
		if (future.getDelay(TimeUnit.NANOSECONDS) > 0) {
			future.setState(TaskState.SLEEPING);
		}
		super.getQueue().add(future);
		return future;
	}

	@Override
	public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit timeUnit) {
		IXotsWrapper<V> wrapper = wrapperFactory_.createWrapper(callable);
		return queue(new XotsFutureTask<V>(wrapper, new PeriodicScheduler(delay, 0L, timeUnit)));
	}

	@Override
	public ScheduledFuture<?> schedule(final Runnable runnable, final long delay, final TimeUnit timeUnit) {
		IXotsWrapper<Object> wrapper = wrapperFactory_.createWrapper(runnable, null);
		return queue(new XotsFutureTask<Object>(wrapper, new PeriodicScheduler(delay, 0L, timeUnit)));
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(final Runnable runnable, final long delay, final long period, final TimeUnit timeUnit) {
		if (period <= 0) {
			throw new IllegalStateException("period must be > 0");
		}
		IXotsWrapper<Object> wrapper = wrapperFactory_.createWrapper(runnable, null);
		return queue(new XotsFutureTask<Object>(wrapper, new PeriodicScheduler(delay, period, timeUnit)));
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable runnable, final long delay, final long period, final TimeUnit timeUnit) {
		if (period <= 0) {
			throw new IllegalStateException("period must be > 0");
		}
		IXotsWrapper<Object> wrapper = wrapperFactory_.createWrapper(runnable, null);
		return queue(new XotsFutureTask<Object>(wrapper, new PeriodicScheduler(delay, -period, timeUnit)));
	}

	@Override
	public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final Scheduler scheduler) {
		IXotsWrapper<V> wrapper = wrapperFactory_.createWrapper(callable);
		return queue(new XotsFutureTask<V>(wrapper, scheduler));
	}

	@Override
	public ScheduledFuture<?> schedule(final Runnable runnable, final Scheduler scheduler) {
		IXotsWrapper<Object> wrapper = wrapperFactory_.createWrapper(runnable, null);
		return queue(new XotsFutureTask<Object>(wrapper, scheduler));
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
		IXotsWrapper<T> wrapper = wrapperFactory_.createWrapper(callable);
		return queue(new XotsFutureTask<T>(wrapper, new PeriodicScheduler(0, 0, TimeUnit.MILLISECONDS)));
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T type) {
		IXotsWrapper<T> wrapper = wrapperFactory_.createWrapper(runnable, type);
		return queue(new XotsFutureTask<T>(wrapper, new PeriodicScheduler(0, 0, TimeUnit.MILLISECONDS)));
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
