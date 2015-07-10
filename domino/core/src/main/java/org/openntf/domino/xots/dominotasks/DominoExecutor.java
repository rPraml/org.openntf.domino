/**
 * 
 */
package org.openntf.domino.thread;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openntf.domino.annotations.Incomplete;

/**
 * A ThreadPoolExecutor for Domino runnables. It sets up a shutdown hook for proper termination. There should be maximum one instance of
 * DominoExecutor, otherwise concurrency won't work
 * 
 * @author Nathan T. Freeman
 */
@Incomplete
package org.openntf.domino.xots.dominotasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openntf.domino.xots.tasks.IWrappedCallable;
import org.openntf.domino.xots.tasks.IWrappedRunnable;
import org.openntf.domino.xots.tasks.Scheduler;
import org.openntf.domino.xots.tasks.impl.PeriodicScheduler;
import org.openntf.domino.xots.tasks.impl.XotsExecutor;

public class DominoExecutor extends XotsExecutor {

	private static final Logger log_ = Logger.getLogger(DominoExecutor.class.getName());

	public DominoExecutor(final int corePoolSize, final String executorName) {
		super(corePoolSize, executorName);
	}

	public DominoExecutor(final int corePoolSize) {
		super(corePoolSize);
	}

	/**
	 * The Callable - Wrapper for Domino Tasks
	 * 
	 * @author Roland Praml, FOCONIS AG
	 * 
	 * @param <V>
	 */
	public static class DominoWrappedCallable<V> extends AbstractWrappedDominoTask implements IWrappedCallable<V> {

		public DominoWrappedCallable(final Callable<V> callable) {
			setWrappedTask(callable);
		}

		@SuppressWarnings("unchecked")
		@Override
		public V call() throws Exception {
			return (V) callOrRun();
		}

	}

	public static class DominoWrappedRunnable extends AbstractWrappedDominoTask implements IWrappedRunnable {

		public DominoWrappedRunnable(final Runnable runnable) {
			setWrappedTask(runnable);
		}

		@Override
		public void run() {
			try {
				callOrRun();
			} catch (Throwable t) {
				log_.log(Level.WARNING, getDescription() + " caused an error: " + t.toString(), t);
				throw new RuntimeException(t);
			}

		}

	}

	@Override
	public int getPriority() {
		return 97;
	}

	@Override
	protected <V> IWrappedCallable<V> wrap(final Callable<V> inner) {
		if (inner instanceof IWrappedCallable)
			return (IWrappedCallable<V>) inner;
		return new DominoWrappedCallable<V>(inner);
	}

	@Override
	protected IWrappedRunnable wrap(final Runnable inner) {
		if (inner instanceof IWrappedRunnable)
			return (IWrappedRunnable) inner;
		return new DominoWrappedRunnable(inner);
	}

	protected IWrappedCallable<?> wrap(final String moduleName, final String className, final Object... ctorArgs) {
		throw new UnsupportedOperationException("Running tasklets is not supported (requires XPage-environment)");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ScheduledFuture<?> scheduleTasklet(final String moduleName, final String className, final Scheduler scheduler,
			final Object... ctorArgs) {
		return queue(new XotsFutureTask(wrap(moduleName, className, ctorArgs), scheduler));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ScheduledFuture<?> runTasklet(final String moduleName, final String className, final Object... ctorArgs) {
		return queue(new XotsFutureTask(wrap(moduleName, className, ctorArgs), new PeriodicScheduler(0, 0L, TimeUnit.NANOSECONDS)));
	}
}

