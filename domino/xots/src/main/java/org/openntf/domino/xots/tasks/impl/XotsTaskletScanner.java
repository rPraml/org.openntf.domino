package org.openntf.domino.xots.tasks.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.openntf.domino.commons.utils.PackageScanner;
import org.openntf.domino.xots.Tasklet;
import org.openntf.domino.xots.Xots;

public class XotsTaskletScanner implements Runnable {

	private String packageName;

	private List<PeriodicTask> periodicTasks = new ArrayList<PeriodicTask>();

	public XotsTaskletScanner(final String packageName) {
		this.packageName = packageName;
	}

	@Override
	public void run() {
		List<Class<?>> classes = PackageScanner.findClasses(packageName);
		for (Class<?> clazz : classes) {
			if (clazz.isAnnotationPresent(Tasklet.class)) {
				String[] schedule = clazz.getAnnotation(Tasklet.class).schedule();
				if (schedule != null && schedule.length > 0) {
					periodicTasks.add(new PeriodicTask(clazz, schedule));
				}
			}
		}

		for (PeriodicTask task : periodicTasks) {
			try {
				Object i = task.getTaskletClass().newInstance();
				for (String schedule : task.getSchedule()) {
					if (i instanceof Runnable) {
						Xots.getService().schedule((Runnable) i, new PeriodicScheduler(schedule));
					} else if (i instanceof Callable) {
						Xots.getService().schedule((Callable<?>) i, new PeriodicScheduler(schedule));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
