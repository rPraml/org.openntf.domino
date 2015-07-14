package org.openntf.tasklet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openntf.domino.commons.ILifeCycle;
import org.openntf.domino.commons.IO;
import org.openntf.tasklet.TaskletExecutor.TaskletFutureTask;

/*
 * This class and package is intended to become the space for the XPages implementation
 * of IBM's DOTS. Except it will use modern thread management instead of acting like it was
 * written in Java 1.1
 */
public class TaskletLifeCylce implements ILifeCycle {

	public static Comparator<TaskletFutureTask<?>> TASKS_BY_ID = new Comparator<TaskletFutureTask<?>>() {
		@Override
		public int compare(final TaskletFutureTask<?> o1, final TaskletFutureTask<?> o2) {
			if (o1.getId() < o2.getId()) {
				return -1;
			} else if (o1.getId() == o2.getId()) {
				return 0;
			} else {
				return 1;
			}
		}
	};
	//private Set<TaskletDefinition> tasklets_ = new HashSet<TaskletDefinition>();

	// This is our Threadpool that will execute all Runnables
	private static TaskletExecutor executor_;

	public static TaskletExecutor getService() {
		if (!isStarted()) {
			throw new IllegalStateException("Xots is not started");
		}
		return executor_;
	}

	public static List<TaskletFutureTask<?>> getTasks(final Comparator<TaskletFutureTask<?>> comparator) {
		if (!isStarted())
			return Collections.emptyList();
		return executor_.getTasks(comparator);
	}

	/**
	 * Tests if the XotsDaemon is started
	 * 
	 */
	public static synchronized boolean isStarted() {
		return executor_ != null;
	}

	public static synchronized void stop(int wait) {
		if (isStarted()) {
			IO.println(TaskletLifeCylce.class, "Stopping XPages OSGi Tasklet Service...");

			executor_.shutdown();
			long running;
			try {
				while ((running = executor_.getActiveCount()) > 0 && wait-- > 0) {
					IO.println(TaskletLifeCylce.class, "There are " + running + " tasks running... waiting " + wait + " seconds.");
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
			}

			if (executor_.getActiveCount() > 0) {
				IO.println(TaskletLifeCylce.class, "he following Threads did not terminate gracefully:");
				for (TaskletFutureTask<?> task : executor_.getTasks(null)) {
					IO.println(TaskletLifeCylce.class, "* " + task);
				}

			}

			try {
				for (int i = 60; i > 0; i -= 10) {
					if (executor_.getActiveCount() > 0) {
						IO.println(TaskletLifeCylce.class, "Trying to interrupt them and waiting again " + i + " seconds.");
						executor_.shutdownNow();
					}
					if (executor_.awaitTermination(10, TimeUnit.SECONDS)) {
						executor_ = null;
						IO.println(TaskletLifeCylce.class, " XPages OSGi Tasklet Service stopped.");
						return;
					}
				}
			} catch (InterruptedException e) {
			}
			IO.println(TaskletLifeCylce.class, "WARNING: Could not stop  XPages OSGi Tasklet Service!");
		} else {
			IO.println(TaskletLifeCylce.class, " XPages OSGi Tasklet Service not running");
		}
	}

	@Override
	public int getPriority() {
		return 99;
	}

	@Override
	public void startup() {
		// TODO read the value from Notes.ini
		executor_ = new TaskletExecutor(10, 50, "Xots");
		IO.println(TaskletLifeCylce.class, "Starting XPages OSGi Tasklet Service with " + executor_.getCorePoolSize() + " core threads.");
	}

	@Override
	public void shutdown() {
		stop(60);
	}

	//
	//	// ---- delegate methods
	//
	///**
	//	 * Registers a new tasklet for periodic execution
	//	 * @param moduleName
	//	 *            the ModuleName (i.e. DatabaseName)
	//	 * @param className
	//	 *            the ClassName. (Must be annotated with {@link XotsTasklet
	//	 * @param onEvent
	//	 * String array with events
	//	 * @return
	//	 */
	//	public static Future<?> registerTasklet(final String moduleName, final String className, final String... cron) {
	//		return executor_.registerTasklet(moduleName, className, cron);
	//
	//	}
}
