package org.openntf.domino.xots.dominotasks;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;

import lotus.domino.NotesThread;

import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.LifeCycleManager;
import org.openntf.domino.session.ISessionFactory;
import org.openntf.domino.thread.DominoRequest;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.utils.Factory.ThreadConfig;
import org.openntf.domino.xots.Tasklet;
import org.openntf.domino.xots.tasks.AbstractWrappedTask;

/**
 * A Wrapper for callables and runnable that provides proper setup/teardown
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public abstract class AbstractWrappedDominoTask extends AbstractWrappedTask {

	protected ISessionFactory sessionFactory;
	protected ThreadConfig threadConfig;
	protected Locale locale;

	/**
	 * Determines the sessionType under which the current runnable should run. The first non-null value of the following list is returned
	 * <ul>
	 * <li>If the runnable implements <code>IDominoRunnable</code>: result of <code>getSessionType</code></li>
	 * <li>the value of {@link SessionType} Annotation</li>
	 * <li>DominoSessionType.DEFAULT</li>
	 * </ul>
	 * 
	 * @param task
	 *            the runnable to determine the DominoSessionType
	 */
	@Override
	protected synchronized void setWrappedTask(final Object task) {
		wrappedTask = task;
		if (task == null)
			return;
		// some security checks...
		if (task instanceof NotesThread) {
			// RPr: I'm not sure if this should be allowed anyway...
			throw new IllegalArgumentException("Cannot wrap the NotesThread " + task.getClass().getName() + " into a DominoRunner");
		}
		// TODO RPr: Review/fix this 
		//				if (task instanceof DominoFutureTask) {
		//					// RPr: don't know if this is possible
		//					throw new IllegalArgumentException("Cannot wrap the WrappedCallable " + task.getClass().getName() + " into a DominoRunner");
		//				}
		if (task instanceof AbstractWrappedTask) {
			// RPr: don't know if this is possible
			throw new IllegalArgumentException("Cannot wrap the WrappedCallable " + task.getClass().getName() + " into a DominoRunner");
		}

		if (task instanceof Tasklet.Interface) {
			Tasklet.Interface taskletInterface = (Tasklet.Interface) task;
			scope = taskletInterface.getScope();
			context = taskletInterface.getContext();
		}
		if (task instanceof AbstractDominoRunnable) {
			AbstractDominoRunnable dominoRunnable = (AbstractDominoRunnable) task;
			sessionFactory = dominoRunnable.getSessionFactory();
			threadConfig = dominoRunnable.getThreadConfig();
		}
		Tasklet annot = task.getClass().getAnnotation(Tasklet.class);

		if (annot != null) {
			if (sessionFactory == null) {
				switch (annot.session()) {
				case CLONE:
					sessionFactory = Factory.getSessionFactory(SessionType.CURRENT);
					break;
				case CLONE_FULL_ACCESS:
					sessionFactory = Factory.getSessionFactory(SessionType.CURRENT_FULL_ACCESS);
					break;

				case FULL_ACCESS:
					sessionFactory = Factory.getSessionFactory(SessionType.FULL_ACCESS);
					break;

				case NATIVE:
					sessionFactory = Factory.getSessionFactory(SessionType.NATIVE);
					break;

				case NONE:
					sessionFactory = null;
					break;

				case SIGNER:
					sessionFactory = Factory.getSessionFactory(SessionType.SIGNER);
					break;

				case SIGNER_FULL_ACCESS:
					sessionFactory = Factory.getSessionFactory(SessionType.SIGNER_FULL_ACCESS);
					break;

				case TRUSTED:
					sessionFactory = Factory.getSessionFactory(SessionType.TRUSTED);
					break;

				default:
					break;
				}
				if ((annot.session() != Tasklet.Session.NONE) && sessionFactory == null) {
					throw new IllegalStateException("Could not create a Fatory for " + annot.session());
				}
				// 30.01.15/RPr: no longer neccessary
				//				if (sessionFactory != null) {
				//					Factory.setSessionFactory(sessionFactory, SessionType.CURRENT);
				//				}
			}

			if (context == null) {
				context = annot.context();
			}
			if (context == null) {
				context = Tasklet.Context.DEFAULT;
			}

			if (scope == null) {
				scope = annot.scope();
			}
			if (scope == null) {
				scope = Tasklet.Scope.NONE;
			}
			if (threadConfig == null) {
				switch (annot.threadConfig()) {
				case CLONE:
					threadConfig = Factory.getThreadConfig();
					break;
				case PERMISSIVE:
					threadConfig = Factory.PERMISSIVE_THREAD_CONFIG;
					break;
				case STRICT:
					threadConfig = Factory.STRICT_THREAD_CONFIG;
					break;
				}
			}
		}

		if (threadConfig == null)
			threadConfig = Factory.getThreadConfig();

		locale = LifeCycleManager.getCurrentRequest().getLocale();

	}

	/**
	 * Returns the wrapped task
	 */
	@Override
	public synchronized Object getWrappedTask() {
		return wrappedTask;
	}

	/**
	 * adds an observer to the wrapped task
	 */
	@Override
	public void addObserver(final Observer o) {
		Object task = getWrappedTask();
		if (task instanceof Observable) {
			((Observable) task).addObserver(o);
		}
	}

	/**
	 * stops the wrapped task if it does implement {@link Tasklet.Interface}
	 */
	@Override
	public void stop() {
		Object task = getWrappedTask();
		if (task instanceof Tasklet.Interface) {
			((Tasklet.Interface) task).stop();
		}
	}

	@Override
	public String getDescription() {
		Object task = getWrappedTask();
		if (task instanceof Tasklet.Interface) {
			return ((Tasklet.Interface) task).getDescription();
		}
		return task.getClass().getSimpleName();
	}

	/**
	 * Common method that does setUp/tearDown before executing the wrapped object
	 * 
	 * @param callable
	 * @param runnable
	 * @return
	 * @throws Exception
	 */
	@Override
	protected Object callOrRun() throws Exception {

		NotesThread.sinitThread();
		IRequest request = new DominoRequest(threadConfig, "&tasklet=" + wrappedTask.getClass().getName(), locale);
		LifeCycleManager.beforeRequest(request);
		try {
			return invokeWrappedTask();
		} finally {
			LifeCycleManager.afterRequest();
			NotesThread.stermThread();
		}
	}

	@Override
	protected Object invokeWrappedTask() throws Exception {
		Thread thread = Thread.currentThread();
		ClassLoader oldCl = thread.getContextClassLoader();
		Object wrappedTask = getWrappedTask();
		ClassLoader runCl = wrappedTask.getClass().getClassLoader();
		thread.setContextClassLoader(runCl);

		if (sessionFactory != null) {
			Factory.setSessionFactory(sessionFactory, SessionType.CURRENT);
		}
		try {
			if (wrappedTask instanceof Callable) {
				return ((Callable<?>) wrappedTask).call();
			} else {
				((Runnable) wrappedTask).run();
				return null;
			}
		} finally {
			thread.setContextClassLoader(oldCl);
		}
	}
}