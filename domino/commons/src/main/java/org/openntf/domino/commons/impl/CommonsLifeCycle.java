package org.openntf.domino.commons.impl;

import org.openntf.domino.commons.StandardLifeCycle;

public class CommonsLifeCycle extends StandardLifeCycle {
	@Override
	public int getPriority() {
		return -1; // this is the first!
	}
}
