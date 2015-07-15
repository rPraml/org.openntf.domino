package org.openntf.domino.xots;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.LifeCycleManager;
import org.openntf.domino.commons.ServiceLocator;
import org.openntf.domino.commons.impl.RequestImpl;
import org.openntf.domino.session.ISessionFactory;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

/**
 * A Wrapper for callables and runnable that provides proper setup/teardown
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public class DominoTaskletWrapper<RET> implements Callable<RET> {

	private ISessionFactory sessionFactory;
	private ThreadInitializer threadInitializer;
	private Callable<RET> inner;
	private IRequest request;

	protected DominoTaskletWrapper(final Callable<RET> callable) {
		inner = callable;
		init(callable);
	}

	protected DominoTaskletWrapper(final Runnable runnable, final RET type) {
		inner = Executors.callable(runnable, type);
		init(runnable);
	}

	public static Runnable wrap(final Runnable r) {
		if (r instanceof DominoTaskletWrapper)
			return r;
		final Callable<Object> wrapper = new DominoTaskletWrapper<Object>(r, null);
		return new Runnable() {

			@Override
			public void run() {
				try {
					wrapper.call();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	public static <T> Callable<T> wrap(final Callable<T> c) {
		if (c instanceof DominoTaskletWrapper)
			return c;
		return new DominoTaskletWrapper<T>(c);
	}

	public static <T> Collection<? extends Callable<T>> wrap(final Collection<? extends Callable<T>> cs) {
		List<Callable<T>> ret = new ArrayList<Callable<T>>(cs.size());
		for (Callable<T> c : cs) {
			ret.add(wrap(c));
		}
		return ret;
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
	protected synchronized void init(final Object task) {
		threadInitializer = ServiceLocator.findApplicationService(ThreadInitializerFactory.class).create();
		request = LifeCycleManager.getCurrentRequest();
		if (request == null) {
			request = new RequestImpl("DominoTasklet: " + task.getClass().getName());
		} else {
			request = request.clone("DominoTasklet: " + task.getClass().getName());
		}
		DominoTasklet annot = task.getClass().getAnnotation(DominoTasklet.class);
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
				if ((annot.session() != DominoTasklet.Session.NONE) && sessionFactory == null) {
					throw new IllegalStateException("Could not create a Fatory for " + annot.session());
				}
			}
		}
	}

	@Override
	public RET call() throws Exception {
		threadInitializer.initThread();
		try {
			LifeCycleManager.beforeRequest(request);
			try {
				Factory.setSessionFactory(sessionFactory, SessionType.CURRENT);
				return inner.call();
			} catch (Exception e) {
				System.err.println("Error while executing " + inner.getClass().getName());
				e.printStackTrace();
				throw e;
			} finally {
				LifeCycleManager.afterRequest();
			}
		} finally {
			threadInitializer.termThread();
		}
	}
}