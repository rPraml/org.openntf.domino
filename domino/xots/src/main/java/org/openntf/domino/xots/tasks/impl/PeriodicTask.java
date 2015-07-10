package org.openntf.domino.xots.tasks.impl;


/**
 * Class that represents a periodic task with the className of the tasklet and the appropriate schedules.
 * 
 * @author Alexander Wagner, FOCONIS AG
 *
 */
public class PeriodicTask {

	private Class<?> taskletClass;
	private String[] schedule;

	public PeriodicTask(final Class<?> taskletClassName, final String[] schedule) {
		this.taskletClass = taskletClassName;
		this.schedule = schedule;
	}

	public Class<?> getTaskletClass() {
		return taskletClass;
	}

	public String[] getSchedule() {
		return schedule;
	}

}
