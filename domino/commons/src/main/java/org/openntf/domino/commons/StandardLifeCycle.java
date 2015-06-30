package org.openntf.domino.commons;

public class StandardLifeCycle implements ILifeCycle, IPriority {

	@Override
	public void startup() {

	}

	@Override
	public void shutdown() {

	}

	@Override
	public int getPriority() {
		return 0; // load commons classes as soon as possible
	}

}
