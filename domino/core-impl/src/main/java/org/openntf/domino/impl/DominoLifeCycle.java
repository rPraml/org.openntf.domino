package org.openntf.domino.impl;

import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.IRequestLifeCycle;
import org.openntf.domino.commons.StandardLifeCycle;

public class DominoLifeCycle extends StandardLifeCycle implements IRequestLifeCycle {

	@Override
	public void beforeRequest(final IRequest request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterRequest() {
		BaseNonThreadSafe.setAllowAccessAcrossThreads(false);
		DateTime.cleanupThread();
	}

}
