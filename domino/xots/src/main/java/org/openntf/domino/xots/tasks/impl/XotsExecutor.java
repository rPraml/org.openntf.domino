/**
 * 
 */
package org.openntf.domino.xots.tasks.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openntf.domino.xots.tasks.AbstractWrappedTask;
import org.openntf.domino.xots.tasks.AbstractXotsExecutor;
import org.openntf.domino.xots.tasks.IWrappedCallable;
import org.openntf.domino.xots.tasks.IWrappedRunnable;
import org.openntf.domino.xots.tasks.Scheduler;

/**
 * A ThreadPoolExecutor for Xots runnables. It sets up a shutdown hook for proper termination. There should be maximum one instance of
 * XotsExecutor, otherwise concurrency won't work
 * 
 * @author Nathan T. Freeman
 */

public class XotsExecutor extends AbstractXotsExecutor {
	private static final Logger log_ = Logger.getLogger(XotsExecutor.class.getName());

	/**
	 * The Callable - Wrapper for Xots Tasks
	 * 
	 * @author Roland Praml, FOCONIS AG
	 * 
	 */
	public static class XotsWrappedCallable<V> extends AbstractWrappedTask implements IWrappedCallable<V> {

		public XotsWrappedCallable(final Callable<V> callable) {
			setWrappedTask(callable);
		}

		@SuppressWarnings("unchecked")
		@Override
		public V call() throws Exception {
			return (V) callOrRun();
		}

		@Override
		protected synchronized void setWrappedTask(final Object task) {
			wrappedTask = task;
		}

		@Override
		protected Object callOrRun() throws Exception {
			throw new UnsupportedOperationException("not yet implemented"); // TODO RPr implement this
			//LifeCycleManager.beforeRequest(taskRequest);
			//			try {
			//				return invokeWrappedTask();
			//			} finally {
			//				LifeCycleManager.afterRequest();
			//			}
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Object invokeWrappedTask() throws Exception {
			Thread thread = Thread.currentThread();
			ClassLoader oldCl = thread.getContextClassLoader();
			Object wrappedTask = getWrappedTask();
			ClassLoader runCl = wrappedTask.getClass().getClassLoader();
			thread.setContextClassLoader(runCl);

			try {
				if (wrappedTask instanceof Callable) {
					return ((Callable<V>) wrappedTask).call();
				}
				return null;
			} finally {
				thread.setContextClassLoader(oldCl);
			}
		}

	}

	/**
	 * The Runnable - Wrapper for Xots Tasks
	 * 
	 * @author Roland Praml, FOCONIS AG
	 * 
	 */
	public static class XotsWrappedRunnable extends AbstractWrappedTask implements IWrappedRunnable {

		public XotsWrappedRunnable(final Runnable runable) {
			setWrappedTask(runable);
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

		@Override
		protected synchronized void setWrappedTask(final Object task) {
			wrappedTask = task;
		}

		@Override
		protected Object callOrRun() throws Exception {
			throw new UnsupportedOperationException("not yet implemented"); // TODO RPr implement this
			//			LifeCycleManager.beforeRequest(taskRequest);
			//			try {
			//				return invokeWrappedTask();
			//			} finally {
			//				LifeCycleManager.afterRequest();
			//			}
		}

		@Override
		protected Object invokeWrappedTask() throws Exception {
			Thread thread = Thread.currentThread();
			ClassLoader oldCl = thread.getContextClassLoader();
			Object wrappedTask = getWrappedTask();
			ClassLoader runCl = wrappedTask.getClass().getClassLoader();
			thread.setContextClassLoader(runCl);

			try {
				if (wrappedTask instanceof Runnable) {
					((Runnable) wrappedTask).run();
				}
				return null;
			} finally {
				thread.setContextClassLoader(oldCl);
			}
		}

	}

	// 		*********************************
	//		* Start of XotsExecutor class *
	//		*********************************

	/**
	 * Constructor of the XotsExecutor
	 * 
	 */
	public XotsExecutor(final int corePoolSize, final String executorName) {
		super(corePoolSize, executorName);
		setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
	}

	public XotsExecutor(final int corePoolSize) {
		this(corePoolSize, "Unnamed");
	}

	/**
	 * The wrap factory method for Callables
	 */
	@Override
	protected <V> IWrappedCallable<V> wrap(final Callable<V> inner) {
		if (inner instanceof IWrappedCallable)
			return (IWrappedCallable<V>) inner;
		return new XotsWrappedCallable<V>(inner);
	}

	/**
	 * The wrap factory method for Runnables
	 */
	@Override
	protected IWrappedRunnable wrap(final Runnable inner) {
		if (inner instanceof IWrappedRunnable)
			return (IWrappedRunnable) inner;
		return new XotsWrappedRunnable(inner);
	}

	@Override
	public int getPriority() {
		return 98;
	}

	@Override
	public ScheduledFuture<?> scheduleTasklet(final String moduleName, final String className, final Scheduler scheduler,
			final Object... ctorArgs) {
		return null;
	}

	@Override
	public ScheduledFuture<?> runTasklet(final String moduleName, final String className, final Object... ctorArgs) {
		return null;
	}

}
