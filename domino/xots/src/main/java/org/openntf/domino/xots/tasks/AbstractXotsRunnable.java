/**
 * 
 */
package org.openntf.domino.xots.tasks;

import java.io.Serializable;
import java.util.Observable;
import java.util.logging.Logger;

import org.openntf.domino.xots.Tasklet;

/**
 * @author Nathan T. Freeman
 * 
 * 
 */
public abstract class AbstractXotsRunnable extends Observable implements Tasklet.Interface, Runnable, Serializable {
	@SuppressWarnings("unused")
	private static final Logger log_ = Logger.getLogger(AbstractXotsRunnable.class.getName());
	private static final long serialVersionUID = 1L;

	private volatile boolean shouldStop_ = false;

	@Override
	public Tasklet.Context getContext() {
		return null;
	}

	@Override
	public Tasklet.Scope getScope() {
		return null;
	}

	@Override
	public String[] getDynamicSchedule() {
		return null;
	}

	/**
	 * Method should be queried in loops to determine if we should stop
	 * 
	 * @return
	 */
	protected synchronized boolean shouldStop() {
		return shouldStop_;
	}

	@Override
	public String getDescription() {
		return getClass().getSimpleName();
	}

	@Override
	public synchronized void stop() {
		shouldStop_ = true;
	}

}
