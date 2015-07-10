package org.openntf.domino.xsp.xots;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;
import org.openntf.domino.commons.LifeCycleManager;
import org.openntf.domino.commons.utils.ThreadUtils;
import org.openntf.domino.config.Configuration;
import org.openntf.domino.config.XotsConfiguration;
import org.openntf.domino.types.Null;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.utils.ODAUtils;
import org.openntf.domino.xots.Tasklet;
import org.openntf.domino.xots.dominotasks.AbstractWrappedDominoTask;
import org.openntf.domino.xsp.ODAPlatform;

import com.ibm.commons.util.ThreadLock;
import com.ibm.domino.xsp.module.nsf.NSFComponentModule;
import com.ibm.domino.xsp.module.nsf.NotesContext;

@Deprecated
public class XotsWrappedTask extends AbstractWrappedDominoTask {

	/**
	 * Common code for the wrappers
	 * 
	 * @param module
	 * @param bubbleException
	 * @param sessionFactory
	 * @param callable
	 * @param runnable
	 * @return
	 */
	protected Object callOrRun(final NSFComponentModule module) throws Exception {
		NSFComponentModule codeModule = null;

		if (module != null) {
			codeModule = module.getTemplateModule() == null ? module : module.getTemplateModule();
			if (module.isDestroyed() || codeModule.isDestroyed()) {
				throw new IllegalArgumentException("Module was destroyed in the meantime. Cannot run");
			}
			module.updateLastModuleAccess();
			codeModule.updateLastModuleAccess();
		}

		final NotesContext ctx = new NotesContext(module);
		NotesContext.initThread(ctx);

		try {
			// checkme: What should we use here?
			//Factory.initThread(ODAPlatform.getAppThreadConfig(module.getNotesApplication()));
			LifeCycleManager.beforeRequest(taskRequest);
			try {
				return invokeTasklet(ctx, codeModule);
			} catch (Exception e) {
				ODAUtils.handleException(e);
				return null;
			} finally {
				LifeCycleManager.afterRequest();
			}
		} finally {
			NotesContext.termThread();
		}
	}

	/**
	 * Invokes the tasklet
	 * 
	 * @param codeModule
	 * @param bubbleException
	 * @param sessionFactory
	 * @param wrappedTask
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("null")
	protected Object invokeTasklet(final NotesContext ctx, final NSFComponentModule module) throws Exception {

		ClassLoader mcl = null;
		ThreadLock readLock = null;
		NSFComponentModule codeModule = null;
		if (module == null) {
			mcl = getWrappedTask().getClass().getClassLoader();
		} else {
			// RPr: In my opinion, This is the proper way how to run runnables in a different thread
			codeModule = module.getTemplateModule();
			if (codeModule == null)
				codeModule = module;

			mcl = codeModule.getModuleClassLoader();
			if (ODAPlatform.isAppFlagSet("LOCKXOTS", codeModule.getNotesApplication())) {
				readLock = XotsDominoExecutor.getLockManager(codeModule).getReadLock();
			}
		}

		if (sessionFactory != null) {
			Factory.setSessionFactory(sessionFactory, SessionType.CURRENT);
			@SuppressWarnings("unused")
			org.openntf.domino.Session current = Factory.getSession(SessionType.CURRENT);

		}

		try {
			if (readLock != null)
				readLock.acquire(); // we want to read data from the module, so lock it!

			// set up the classloader
			ClassLoader oldCl = ThreadUtils.setContextClassLoader(mcl);
			try {
				Object wrappedTask = getWrappedTask();
				XotsDominoExecutor.initModule(ctx, mcl, wrappedTask);

				XotsConfiguration config = null;
				if (mcl instanceof BaseClassLoader) {
					// Determine the bundle of mcl
					String bundle = ((BaseClassLoader) mcl).getClasspathManager().getBaseData().getSymbolicName();
					config = Configuration.getXotsBundleConfiguration(bundle, wrappedTask.getClass().getName());
				} else {
					config = Configuration.getXotsNSFConfiguration(module.getDatabasePath(), wrappedTask.getClass().getName());
				}

				try {
					config.logStart();
					Object ret = invokeObject(wrappedTask);
					config.logSuccess();
					return ret;
				} catch (Exception e) {
					config.logError(e);
					throw e;
				}

			} finally {
				ThreadUtils.setContextClassLoader(oldCl);
			}
		} finally {
			if (readLock != null)
				readLock.release();
		}

	}

	protected Object invokeObject(final Object wrappedTask) throws Exception {
		if (wrappedTask instanceof Callable) {
			return ((Callable<?>) wrappedTask).call();
		} else {
			((Runnable) wrappedTask).run();
			return null;
		}
	}

	/**
	 * Finds the constructor for the given Tasklet
	 * 
	 * @param clazz
	 * @param args
	 * @return
	 */
	protected Constructor<?> findConstructor(final Class<?> clazz, final Object[] args) {
		// sanity check if this is a public tasklet
		Tasklet annot = clazz.getAnnotation(Tasklet.class);
		if (annot == null) {
			throw new IllegalStateException("Cannot run " + clazz.getName() + ", because it does not annotate @Tasklet.");
		}

		//		if (!(Callable.class.isAssignableFrom(clazz)) && !(Runnable.class.isAssignableFrom(clazz))) {
		//			throw new IllegalStateException("Cannot run " + clazz.getName() + ", because it is no Runnable or Callable.");
		//		}

		// find the constructor
		Class<?> ctorClasses[] = new Class<?>[args.length];
		Object ctorArgs[] = new Object[args.length];
		for (int i = 0; i < ctorClasses.length; i++) {
			Object arg;
			ctorArgs[i] = arg = args[i];
			ctorClasses[i] = arg == null ? Null.class : arg.getClass();
		}

		Constructor<?> cTor = null;
		try {
			cTor = clazz.getConstructor(ctorClasses);
		} catch (NoSuchMethodException nsme1) {
			try {
				cTor = clazz.getConstructor(new Class<?>[] { Object[].class });
				ctorArgs = new Object[] { ctorArgs };
			} catch (NoSuchMethodException nsme2) {

			}
		}
		if (cTor == null) {
			throw new IllegalStateException("Cannot run " + clazz.getName() + ", because it has no constructor for Arguments: " + ctorArgs);
		}
		return cTor;
	}
}