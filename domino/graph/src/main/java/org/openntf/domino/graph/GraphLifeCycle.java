package org.openntf.domino.graph;

import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.IRequestLifeCycle;
import org.openntf.domino.commons.StandardLifeCycle;

public class GraphLifeCycle extends StandardLifeCycle implements IRequestLifeCycle {

	@Override
	public void beforeRequest(final IRequest request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterRequest() {
		DominoGraph.clearDocumentCache();
	}

}
