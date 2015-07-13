package org.openntf.domino.xots.tasks;

import java.util.Calendar;

public interface Scheduler {

	public void eventStart(Calendar now);

	public void eventStop(Calendar now);

	public boolean isPeriodic();

	public long getNextExecutionTimeInMillis();

}
