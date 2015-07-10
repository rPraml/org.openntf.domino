package org.openntf.domino.xots.tasks.impl;

import org.openntf.domino.commons.IDateTime;

public class PeriodicTask {

	private Class<?> taskletClass;
	private IDateTime lastExecution;
	private String[] schedule;

	public PeriodicTask(final Class<?> taskletClassName, final String[] schedule) {
		this.taskletClass = taskletClassName;
		this.schedule = schedule;
	}

	public void setLastExecution(final IDateTime lastExecution) {
		this.lastExecution = lastExecution;
	}

	public IDateTime getLastExecution() {
		return lastExecution;
	}

	public Class<?> getTaskletClass() {
		return taskletClass;
	}

	public String[] getSchedule() {
		return schedule;
	}
}
