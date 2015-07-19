package org.openntf.domino.commons;

/**
 * This is the lifecycle interface. Every jar in classpath can register various life cycle classes in the
 * META-INF/services/org.openntf.domino.commons.ILifeCycle file.
 * 
 * LifeCycles are started according its priority.
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public interface ILifeCycle extends IPriority {

	void startup();

	void shutdown();

}
