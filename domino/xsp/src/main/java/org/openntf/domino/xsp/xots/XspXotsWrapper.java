package org.openntf.domino.xsp.xots;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Callable;

import lotus.notes.NotesThread;

import org.openntf.domino.commons.utils.ThreadUtils;
import org.openntf.domino.session.ISessionFactory;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.xsp.ODAPlatform;
import org.openntf.domino.xsp.session.InvalidSessionFactory;
import org.openntf.domino.xsp.session.XPageNamedSessionFactory;
import org.openntf.domino.xsp.session.XPageSignerSessionFactory;
import org.openntf.tasklet.Tasklet;
import org.openntf.tasklet.impl.TaskletWrapper;

import com.ibm.commons.util.ThreadLock;
import com.ibm.commons.util.ThreadLockManager;
import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.domino.xsp.module.nsf.ModuleClassLoader;
import com.ibm.domino.xsp.module.nsf.NSFComponentModule;
import com.ibm.domino.xsp.module.nsf.NotesContext;

/**
 * A Wrapper for callables and runnable that provides proper setup/teardown
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public class XspXotsWrapper<RET> extends TaskletWrapper<RET> {

	private ISessionFactory sessionFactory;
	private Context ctx;

	public XspXotsWrapper(final Callable<RET> callable) {
		super(callable);
	}

	public XspXotsWrapper(final Runnable runnable, final RET ret) {
		super(runnable, ret);
	}

	/**
	 * We lock the module, so that it cannot get refreshed. (hope this is a good idea)
	 * 
	 */
	private static Field lockManager_field = AccessController.doPrivileged(new PrivilegedAction<Field>() {
		@Override
		public Field run() {
			Field field;
			try {
				field = ComponentModule.class.getDeclaredField("lockManager");
				field.setAccessible(true);
				return field;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	});

	/**
	 * Returns the lock manager for the given ComponentModule
	 * 
	 * @param module
	 * @return
	 */
	static ThreadLockManager getLockManager(final ComponentModule module) {
		try {
			if (lockManager_field != null) {
				return (ThreadLockManager) lockManager_field.get(module);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static class Context {
		private ClassLoader classLoader;
		public NSFComponentModule module;

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
		// Determine where we are:
		ctx = getCurrentContext(task);
	}

	private Context getCurrentContext(final Object task) {
		Context ret = new Context();

		NotesContext ctx = NotesContext.getCurrentUnchecked();
		if (ctx == null) {
			ret.classLoader = ThreadUtils.getContextClassLoader();
		} else {
			ret.module = ctx.getModule();
		}
		return ret;
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
		ThreadLock readLock = null;
		NotesContext notesCtx = null;

		if (ctx.module != null) {
			if (ODAPlatform.isAppFlagSet("LOCKXOTS", ctx.module.getNotesApplication())) {
				readLock = getLockManager(ctx.module).getReadLock();
			}
			notesCtx = new NotesContext(ctx.module);
			classLoader = ctx.module.getModuleClassLoader();
			NotesContext.initThread(notesCtx);

		} else {
			NotesThread.sinitThread();
		}

		try {
			if (notesCtx != null) {
				// Check who has signed the class
				URLClassLoader dcl = (URLClassLoader) ((ModuleClassLoader) classLoader).getDynamicClassLoader();
				String className = wrappedObject.getClass().getName();
				String str = className.replace('.', '/') + ".class";
				URL url = dcl.findResource(str);
				if (url != null && url.getProtocol().startsWith("xspnsf")) {
					// Set up the "TopLevelXPageSigner == Signer of the runnable
					// As soon as we are in a xspnsf, we do not have a SessionFactory!

					notesCtx.setSignerSessionRights("WEB-INF/classes/" + str);
					Factory.setSessionFactory(new XPageSignerSessionFactory(false), SessionType.SIGNER);
					Factory.setSessionFactory(new XPageSignerSessionFactory(true), SessionType.SIGNER_FULL_ACCESS);
				} else {
					// RPr: There is a bug: you can decide if you want to use "sessionAsSigner" or "sessionAsSignerFullAccess"
					// But you cannot use both simultaneously!
					// do not setup signer sessions if it is not properly signed!
					Factory.setSessionFactory(new InvalidSessionFactory(), SessionType.SIGNER);
					Factory.setSessionFactory(new InvalidSessionFactory(), SessionType.SIGNER_FULL_ACCESS);
				}

			} else {
				// The code is not part from an NSF, so it resides on the server
				Factory.setSessionFactory(new XPageNamedSessionFactory(Factory.getLocalServerName(), false), SessionType.SIGNER);
				Factory.setSessionFactory(new XPageNamedSessionFactory(Factory.getLocalServerName(), true), SessionType.SIGNER_FULL_ACCESS);
			}

			// lock the module!
			if (readLock != null)
				readLock.acquire(); // we want to read data from the module, so lock it!
			try {
				ClassLoader oldCl = ThreadUtils.setContextClassLoader(classLoader);
				try {
					return super.call();
				} finally {
					ThreadUtils.setContextClassLoader(oldCl);
				}
			} finally {
				if (readLock != null)
					readLock.acquire(); // we want to read data from the module, so lock it!
			}
		} finally {
			if (ctx.module != null) {
				NotesContext.termThread();
			} else {
				NotesThread.stermThread();
			}
		}
	}
}