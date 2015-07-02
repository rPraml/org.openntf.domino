package org.openntf.domino.commons;

import java.util.ArrayList;
import java.util.List;

import org.openntf.domino.commons.utils.BundleInfos;

public enum LifeCycleManager {
	;
	private static int startups = 0;

	private static List<ILifeCycle> lazyLifeCycles = new ArrayList<ILifeCycle>();
	private static List<IRequestLifeCycle> lazyRequestLifeCycles = new ArrayList<IRequestLifeCycle>();
	private static List<ILifeCycle> serviceLifeCycles;

	private static ThreadLocal<List<Runnable>> shutdownHooks = new ThreadLocal<List<Runnable>>() {
		@Override
		protected java.util.List<Runnable> initialValue() {
			return new ArrayList();
		}
	};

	public static void startup() {
		synchronized (LifeCycleManager.class) {
			if (startups > 0) {
				startups++;
				return;
			}
			serviceLifeCycles = ServiceLocator.findApplicationServices(ILifeCycle.class);
			try {
				for (ILifeCycle service : serviceLifeCycles) {
					track(service);
					service.startup();
				}
				startups = 1;
				ILifeCycle[] copy;
				synchronized (lazyLifeCycles) {
					copy = lazyLifeCycles.toArray(new ILifeCycle[lazyLifeCycles.size()]);
				}
				for (ILifeCycle lifecycle : copy) {
					lifecycle.startup();
				}
				IO.println("LifeCycle", "All services started successfully");
			} catch (Throwable t) {
				t.printStackTrace();
				IO.println("LifeCycle", "There were errors while starting services");
			}

		}
	}

	private static void track(final ILifeCycle service) {
		BundleInfos bi = BundleInfos.getInstance(service.getClass());
		IO.println("LifeCycle", "Using bundle '" + bi.getBundleName() + "' (Version:" + bi.getBundleVersion() + ")");
		IO.printDbg("    GIT-Version:        " + bi.getBuildVersion());
		// output some GIT statistics
		IO.printDbg("    Commit-ID:          " + bi.getCommitId());
		IO.printDbg("    Commit-ID-Describe: " + bi.getCommitIdDescribe());
		IO.printDbg("    Commit-Timestamp:   " + bi.getCommitTime());
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
	 * Add a hook that will run on shutdown
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

	public static void afterRequest() {

		List<Runnable> hooks = shutdownHooks.get();
		Runnable[] hookCopy = hooks.toArray(new Runnable[hooks.size()]);
		hooks.clear();

		for (Runnable hook : hookCopy) {
			try {
				hook.run();
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
		for (IRequestLifeCycle lifecycle : ServiceLocator.findApplicationServices(IRequestLifeCycle.class)) {
			try {
				lifecycle.afterRequest();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public static void addCleanupHook(final Runnable hook) {
		shutdownHooks.get().add(hook);
	}

	public static void removeCleanupHook(final Runnable hook) {
		shutdownHooks.get().remove(hook);
	}
}
