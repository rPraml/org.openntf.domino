/**
 * 
 */
package org.openntf.domino.xots.dominotasks;

import java.util.logging.Logger;

import org.openntf.domino.session.ISessionFactory;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.xots.tasks.AbstractXotsRunnable;

/**
 * @author Nathan T. Freeman
 * 
 * 
 */
public abstract class AbstractDominoRunnable extends AbstractXotsRunnable {
	@SuppressWarnings("unused")
	private static final Logger log_ = Logger.getLogger(AbstractDominoRunnable.class.getName());
	private static final long serialVersionUID = 1L;

	private ISessionFactory sessionFactory_;

	public ISessionFactory getSessionFactory() {
		return sessionFactory_;
	}

	public Factory.ThreadConfig getThreadConfig() {
		return null;
	}
}
