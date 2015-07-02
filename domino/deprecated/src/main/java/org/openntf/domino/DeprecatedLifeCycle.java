package org.openntf.domino;

import org.openntf.domino.commons.StandardLifeCycle;

public class DeprecatedLifeCycle extends StandardLifeCycle {
	@Override
	public int getPriority() {
		return 1000;
	}
}
