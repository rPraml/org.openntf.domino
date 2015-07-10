/**
 * 
 */
package org.openntf.domino.xots.dominotasks;

import java.util.logging.Logger;

import org.openntf.domino.session.ISessionFactory;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.xots.tasks.AbstractXotsCallable;

/**
 * An observable callable implementation. In your implementation you should check {@link #shouldStop()} periodically
 * 
 * @author Nathan T. Freeman
 */
@SuppressWarnings("unused")
public abstract class AbstractDominoCallable<T> extends AbstractXotsCallable<T> {
	private static final Logger log_ = Logger.getLogger(AbstractDominoCallable.class.getName());
	private static final long serialVersionUID = 1L;

	private boolean shouldStop_ = false;
	private Thread runningThread_;

	public ISessionFactory getSessionFactory() {
		return null;
	}

	public Factory.ThreadConfig getThreadConfig() {
		return null;
	}

}
