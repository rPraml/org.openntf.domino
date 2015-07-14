package org.openntf.tasklet;

/**
 * The Scheduler interface
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public interface Scheduler {

	/**
	 * Event is called when task starts.
	 */
	public void eventStart();

	/**
	 * Event is called when task stops
	 */
	public void eventStop();

	/**
	 * Returns true, if this is a periodic task
	 */
	public boolean isPeriodic();

	/**
	 * This is the Timestamp when the next execution is triggered
	 */
	public long getNextExecutionTimeInMillis();

}
