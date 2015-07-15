package org.openntf.domino.xots;

import static org.openntf.domino.xots.DominoTaskletWrapper.wrap;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openntf.domino.commons.ILifeCycle;
import org.openntf.domino.commons.IO;

/*
 * This class and package is intended to become the space for the XPages implementation
 * of IBM's DOTS. Except it will use modern thread management instead of acting like it was
 * written in Java 1.1
 */
public class Xots implements ILifeCycle, ScheduledExecutorService {
	private static final Logger log_ = Logger.getLogger(Xots.class.getName());

	private ScheduledThreadPoolExecutor delegate;

	private static Xots INSTANCE;

	public static ScheduledExecutorService getService() {
		if (INSTANCE == null) {
			throw new IllegalStateException("OpenNTF Domino Tasklet Service is not running");
		}
		return INSTANCE;
	}

	@Override
	public void execute(final Runnable r) {
		delegate.execute(wrap(r));
	}

	@Override
	public ScheduledFuture<?> schedule(final Runnable r, final long paramLong, final TimeUnit paramTimeUnit) {
		return delegate.schedule(wrap(r), paramLong, paramTimeUnit);
	}

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

	@Override
	public void startup() {
		delegate = new ScheduledThreadPoolExecutor(50, createThreadFactory());
		INSTANCE = this;
	}

	@Override
	public void shutdown() {
		INSTANCE = null;
		if (delegate != null) {
			IO.println(Xots.class, "Stopping OpenNTF Domino Tasklet Service...");
			delegate.shutdown();
			long running;
			int wait = 60; // wait 60 seconds for finish
			try {
				while ((running = delegate.getActiveCount()) > 0 && wait-- > 0) {
					IO.println(Xots.class, "There are " + running + " tasks running... waiting " + wait + " seconds.");
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
			}

			if (delegate.getActiveCount() > 0) {
				IO.println(Xots.class, "There are " + delegate.getActiveCount() + " that did not terminate gracefully.");
			}

			try {
				for (int i = 60; i > 0; i -= 10) {
					if (delegate.getActiveCount() > 0) {
						IO.println(Xots.class, "Trying to interrupt them and waiting again " + i + " seconds.");
						delegate.shutdownNow();
					}
					if (delegate.awaitTermination(10, TimeUnit.SECONDS)) {
						delegate = null;
						IO.println(Xots.class, "OpenNTF Domino Tasklet Service stopped.");
						return;
					}
				}
			} catch (InterruptedException e) {
			}
			delegate = null;
			IO.println(Xots.class, "WARNING: Could not stop OpenNTF Domino Tasklet Service!");
		} else {
			IO.println(Xots.class, "OpenNTF Domino Tasklet Service not running");
		}

	}

	@Override
	public List<Runnable> shutdownNow() {
		return delegate.shutdownNow();
	}

	@Override
	public <V> ScheduledFuture<V> schedule(final Callable<V> c, final long paramLong, final TimeUnit paramTimeUnit) {
		return delegate.schedule(wrap(c), paramLong, paramTimeUnit);
	}

	@Override
	public boolean isShutdown() {
		return delegate.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return delegate.isTerminated();
	}

	@Override
	public boolean awaitTermination(final long paramLong, final TimeUnit paramTimeUnit) throws InterruptedException {
		return delegate.awaitTermination(paramLong, paramTimeUnit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long paramLong1, final long paramLong2,
			final TimeUnit paramTimeUnit) {
		return delegate.scheduleAtFixedRate(wrap(r), paramLong1, paramLong2, paramTimeUnit);
	}

	@Override
	public <T> Future<T> submit(final Callable<T> c) {
		return delegate.submit(wrap(c));
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable r, final long paramLong1, final long paramLong2,
			final TimeUnit paramTimeUnit) {
		return delegate.scheduleWithFixedDelay(wrap(r), paramLong1, paramLong2, paramTimeUnit);
	}

	@Override
	public <T> Future<T> submit(final Runnable r, final T paramT) {
		return delegate.submit(wrap(r), paramT);
	}

	@Override
	public Future<?> submit(final Runnable r) {
		return delegate.submit(wrap(r));
	}

	@Override
	public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> cs) throws InterruptedException {
		return delegate.invokeAll(wrap(cs));
	}

	@Override
	public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> cs, final long paramLong, final TimeUnit paramTimeUnit)
			throws InterruptedException {
		return delegate.invokeAll(wrap(cs), paramLong, paramTimeUnit);
	}

	@Override
	public <T> T invokeAny(final Collection<? extends Callable<T>> cs) throws InterruptedException, ExecutionException {
		return delegate.invokeAny(wrap(cs));
	}

	@Override
	public <T> T invokeAny(final Collection<? extends Callable<T>> cs, final long paramLong, final TimeUnit paramTimeUnit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return delegate.invokeAny(wrap(cs), paramLong, paramTimeUnit);
	}

	@Override
	public int getPriority() {
		return 200;
	}

	//	//private Set<TaskletDefinition> tasklets_ = new HashSet<TaskletDefinition>();
	//
	//	// This is our Threadpool that will execute all Runnables
	//	private static TaskletExecutor executor_;
	//
	//	public static TaskletExecutor getService() {
	//		if (!isStarted()) {
	//			throw new IllegalStateException("Xots is not started");
	//		}
	//		return executor_;
	//	}
	//
	//	public static List<TaskletFutureTask<?>> getTasks(final Comparator<TaskletFutureTask<?>> comparator) {
	//		if (!isStarted())
	//			return Collections.emptyList();
	//		return executor_.getTasks(comparator);
	//	}
	//
	//	/**
	//	 * Tests if the XotsDaemon is started
	//	 * 
	//	 */
	//	public static synchronized boolean isStarted() {
	//		return executor_ != null;
	//	}
	//
	//	public static synchronized void stop(int wait) {
	//		if (isStarted()) {
	//			IO.println(TaskletLifeCylce.class, "Stopping XPages OSGi Tasklet Service...");
	//
	//			executor_.shutdown();
	//			long running;
	//			try {
	//				while ((running = executor_.getActiveCount()) > 0 && wait-- > 0) {
	//					IO.println(TaskletLifeCylce.class, "There are " + running + " tasks running... waiting " + wait + " seconds.");
	//					Thread.sleep(1000);
	//				}
	//			} catch (InterruptedException e) {
	//			}
	//
	//			if (executor_.getActiveCount() > 0) {
	//				IO.println(TaskletLifeCylce.class, "he following Threads did not terminate gracefully:");
	//				for (TaskletFutureTask<?> task : executor_.getTasks(null)) {
	//					IO.println(TaskletLifeCylce.class, "* " + task);
	//				}
	//
	//			}
	//
	//			try {
	//				for (int i = 60; i > 0; i -= 10) {
	//					if (executor_.getActiveCount() > 0) {
	//						IO.println(TaskletLifeCylce.class, "Trying to interrupt them and waiting again " + i + " seconds.");
	//						executor_.shutdownNow();
	//					}
	//					if (executor_.awaitTermination(10, TimeUnit.SECONDS)) {
	//						executor_ = null;
	//						IO.println(TaskletLifeCylce.class, " XPages OSGi Tasklet Service stopped.");
	//						return;
	//					}
	//				}
	//			} catch (InterruptedException e) {
	//			}
	//			IO.println(TaskletLifeCylce.class, "WARNING: Could not stop  XPages OSGi Tasklet Service!");
	//		} else {
	//			IO.println(TaskletLifeCylce.class, " XPages OSGi Tasklet Service not running");
	//		}
	//	}
	//
	//	@Override
	//	public int getPriority() {
	//		return 99;
	//	}
	//
	//	@Override
	//	public void startup() {
	//		// TODO read the value from Notes.ini
	//		executor_ = new TaskletExecutor(10, 50, "Xots");
	//		IO.println(TaskletLifeCylce.class, "Starting XPages OSGi Tasklet Service with " + executor_.getCorePoolSize() + " core threads.");
	//	}
	//
	//	@Override
	//	public void shutdown() {
	//		stop(60);
	//	}
	//
	//	//
	//	//	// ---- delegate methods
	//	//
	//	///**
	//	//	 * Registers a new tasklet for periodic execution
	//	//	 * @param moduleName
	//	//	 *            the ModuleName (i.e. DatabaseName)
	//	//	 * @param className
	//	//	 *            the ClassName. (Must be annotated with {@link XotsTasklet
	//	//	 * @param onEvent
	//	//	 * String array with events
	//	//	 * @return
	//	//	 */
	//	//	public static Future<?> registerTasklet(final String moduleName, final String className, final String... cron) {
	//	//		return executor_.registerTasklet(moduleName, className, cron);
	//	//
	//	//	}
}
