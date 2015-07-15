/*
 * Copyright 2015 - FOCONIS AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express o 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 */
package org.openntf.tasklet.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.openntf.domino.commons.utils.PackageScanner;
import org.openntf.tasklet.Tasklet;

/**
 * This Scanner scans all classes annotated with &#64;{@link Tasklet}
 * 
 * @author Alexander Wagner, FOCONIS AG
 *
 */
public class TaskletScanner implements Runnable {

	// TODO: We should specify also the classLoader
	private String packageName;

	private List<PeriodicTask> periodicTasks = new ArrayList<PeriodicTask>();

	public TaskletScanner(final String packageName) {
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
		// TODO Maybe we can do this better (do not keep instances, it won't work when classloader unloads)!
		for (PeriodicTask task : periodicTasks) {
			try {
				Object i = task.getTaskletClass().newInstance();
				for (String schedule : task.getSchedule()) {
					if (i instanceof Runnable) {
						// TODO Xots.getService().schedule((Runnable) i, new PeriodicScheduler(schedule));
					} else if (i instanceof Callable) {
						// TODO Xots.getService().schedule((Callable<?>) i, new PeriodicScheduler(schedule));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
