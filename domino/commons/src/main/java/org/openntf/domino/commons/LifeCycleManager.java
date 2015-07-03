package org.openntf.domino.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openntf.domino.commons.utils.BundleInfos;

public enum LifeCycleManager {
	;
	private static int startups = 0;

	private static List<ILifeCycle> serviceLifeCycles;
	private static List<ILifeCycle> lazyLifeCycles = new CopyOnWriteArrayList<ILifeCycle>();
	private static List<IRequestLifeCycle> lazyRequestLifeCycles = new CopyOnWriteArrayList<IRequestLifeCycle>();
	private static Set<String> seenBundles = new ConcurrentSkipListSet<String>();
	private static ThreadLocal<List<Runnable>> requestEndHooks = new ThreadLocal<List<Runnable>>() {
		@Override
		protected java.util.List<Runnable> initialValue() {
			return new ArrayList<Runnable>();
		}
	};

	/**
	 * find all LifeCycles and starts them
	 */
	public static void startup() {
		synchronized (LifeCycleManager.class) {
			if (startups > 0) {
				startups++;
				return;
			}
			serviceLifeCycles = ServiceLocator.findApplicationServices(ILifeCycle.class);
			seenBundles.clear();
			try {
				for (ILifeCycle service : serviceLifeCycles) {
					startup(service);
				}

				startups = 1;
				ILifeCycle[] copy;
				synchronized (lazyLifeCycles) {
					copy = lazyLifeCycles.toArray(new ILifeCycle[lazyLifeCycles.size()]);
				}
				for (ILifeCycle service : copy) {
					startup(service);
				}
				IO.println("LifeCycle", "All services started successfully");
			} catch (Throwable t) {
				t.printStackTrace();
				IO.println("LifeCycle", "There were errors while starting services");
			}

		}
	}

	/**
	 * Startup the given lifecycle
	 */
	private static void startup(final ILifeCycle service) {
		service.startup();
		if (service instanceof IRequestLifeCycle) {
			addRequestLifeCycle((IRequestLifeCycle) service);
		}
		BundleInfos bi = BundleInfos.getInstance(service.getClass());
		String symName = bi.getBundleSymbolicName();
		if (symName == null) {
			IO.printDbg("No BundleSymbolicName found for: " + service.getClass().getName());
			return;
		}
		if (seenBundles.add(symName)) {
			IO.println("LifeCycle", "Using bundle '" + bi.getBundleName() + "' (Version:" + bi.getBundleVersion() + ")");
			IO.printDbg("    GIT-Version:        " + bi.getBuildVersion());
			// output some GIT statistics
			IO.printDbg("    Commit-ID:          " + bi.getCommitId());
			IO.printDbg("    Commit-ID-Describe: " + bi.getCommitIdDescribe());
			IO.printDbg("    Commit-Timestamp:   " + bi.getCommitTime());
		}
	}

	public static void shutdown() {
		synchronized (LifeCycleManager.class) {
			if (startups > 1)
				return;
			if (startups != 1)
				throw new IllegalStateException("LifeCycleManager.shutdown() was called more than Factory.startup()");
			try {
				ILifeCycle[] copy;
				synchronized (lazyLifeCycles) {
					copy = lazyLifeCycles.toArray(new ILifeCycle[lazyLifeCycles.size()]);
				}
				for (ILifeCycle lifecycle : copy) {
					try {
						lifecycle.shutdown();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				// TODO Should we clear the list? RPr thinks no
				// lazyLifeCycles = new ArrayList<ILifeCycle>();
				synchronized (lazyLifeCycles) {
					lazyLifeCycles.clear();
				}

				for (ILifeCycle service : serviceLifeCycles) {
					service.shutdown();
				}
				IO.println("LifeCycle", "All services shut down successfully");
			} catch (Throwable t) {
				t.printStackTrace();
				IO.println("LifeCycle", "There were errors while shutting down services");
			} finally {
				startups--;
			}
		}
	}

	/**
	 * Add a hook that will run on shutdown
	 */
	public static void addLifeCycle(final ILifeCycle hook) {
		synchronized (lazyLifeCycles) {
			lazyLifeCycles.add(hook);
			if (startups > 0)
				hook.startup();
		}
	}

	/**
	 * Remove a shutdown hook
	 * 
	 * @param hook
	 *            the hook that should be removed
	 */
	public static void removeLifeCycle(final ILifeCycle hook) {
		synchronized (lazyLifeCycles) {
			lazyLifeCycles.remove(hook);
		}
	}

	/**
	 * Add a hook that will run on request end
	 */
	public static void addRequestLifeCycle(final IRequestLifeCycle hook) {
		synchronized (lazyRequestLifeCycles) {
			lazyRequestLifeCycles.add(hook);
		}
	}

	/**
	 * Remove a shutdown hook
	 * 
	 * @param hook
	 *            the hook that should be removed
	 */
	public static void removeRequestLifeCycle(final IRequestLifeCycle hook) {
		synchronized (lazyRequestLifeCycles) {
			lazyRequestLifeCycles.remove(hook);
		}
	}

	/**
	 * Must be called before each request (Formerly Factory.initThread())
	 */
	public static void beforeRequest(final IRequest request) {
		IRequestLifeCycle[] copy;
		synchronized (lazyRequestLifeCycles) {
			copy = lazyRequestLifeCycles.toArray(new IRequestLifeCycle[lazyLifeCycles.size()]);
		}
		for (IRequestLifeCycle lifecycle : copy) {
			try {
				lifecycle.beforeRequest(request);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		for (IRequestLifeCycle lifecycle : ServiceLocator.findApplicationServices(IRequestLifeCycle.class)) {
			try {
				lifecycle.beforeRequest(request);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	/**
	 * Must be called after each request (Formerly Factory.termThread())
	 */
	public static void afterRequest() {

		List<Runnable> hooks = requestEndHooks.get();
		Runnable[] hookCopy = hooks.toArray(new Runnable[hooks.size()]);
		hooks.clear();

		for (Runnable hook : hookCopy) {
			try {
				hook.run();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		for (IRequestLifeCycle lifecycle : ServiceLocator.findApplicationServices(IRequestLifeCycle.class)) {
			try {
				lifecycle.afterRequest();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		IRequestLifeCycle[] copy;
		synchronized (lazyRequestLifeCycles) {
			copy = lazyRequestLifeCycles.toArray(new IRequestLifeCycle[lazyLifeCycles.size()]);
		}
		for (IRequestLifeCycle lifecycle : copy) {
			try {
				lifecycle.afterRequest();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @param hook
	 */
	public static void onRequestEnd(final Runnable hook) {
		requestEndHooks.get().add(hook);
	}

}
