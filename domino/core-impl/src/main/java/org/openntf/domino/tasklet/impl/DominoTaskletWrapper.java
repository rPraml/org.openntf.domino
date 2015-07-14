package org.openntf.domino.tasklet.impl;

import java.util.concurrent.Callable;

import lotus.domino.NotesThread;

import org.openntf.domino.session.ISessionFactory;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.tasklet.Tasklet;
import org.openntf.tasklet.impl.TaskletWrapper;

/**
 * A Wrapper for callables and runnable that provides proper setup/teardown
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public class DominoTaskletWrapper<RET> extends TaskletWrapper<RET> {

	private ISessionFactory sessionFactory;

	public DominoTaskletWrapper(final Callable<RET> callable) {
		super(callable);
	}

	public DominoTaskletWrapper(final Runnable runnable, final RET ret) {
		super(runnable, ret);
	}

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
	protected synchronized void init(final Object task) {
		super.init(task);
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
			}
		}
	}

	@Override
	protected void beforeCall() {
		super.beforeCall();
		if (sessionFactory != null) {
			Factory.setSessionFactory(sessionFactory, SessionType.CURRENT);
		}
	}

	@Override
	public RET call() throws Exception {
		NotesThread.sinitThread();
		try {
			return super.call();
		} finally {
			NotesThread.stermThread();
		}
	}
}