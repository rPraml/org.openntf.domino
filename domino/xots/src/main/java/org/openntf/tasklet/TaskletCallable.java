/**
 * 
 */
package org.openntf.tasklet;

import java.io.Serializable;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.openntf.domino.xots.Tasklet;

/**
 * An observable callable implementation. In your implementation you should check {@link #shouldStop()} periodically.
 * 
 * This is the base class of a Tasklet-Callable
 * 
 * @author Nathan T. Freeman
 */
@SuppressWarnings("unused")
public abstract class TaskletCallable<T> extends Observable implements Tasklet.Interface, Callable<T>, Serializable {
	private static final Logger log_ = Logger.getLogger(TaskletCallable.class.getName());
	private static final long serialVersionUID = 1L;

	private boolean shouldStop_ = false;
	private Thread runningThread_;

	@Override
	public Tasklet.Scope getScope() {
		return null;
	}

	@Override
	public String[] getDynamicSchedule() {
		return null;
	}

	/**
	 * Returns true Method should be queried in loops to determine if we should stop
	 * 
	 * @return
	 */
	protected synchronized boolean shouldStop() {
		return shouldStop_;
	}

	@Override
	public synchronized void stop() {
		shouldStop_ = true;
	}

	@Override
	public String getDescription() {
		return getClass().getSimpleName();
	}

}
