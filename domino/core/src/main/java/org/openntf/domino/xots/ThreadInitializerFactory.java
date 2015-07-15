package org.openntf.domino.xots;

import org.openntf.domino.commons.IPriority;

public class ThreadInitializerFactory implements IPriority {

	ThreadInitializer create() {
		return new ThreadInitializer();
	}

	@Override
	public int getPriority() {
		return 99;
	}

}
