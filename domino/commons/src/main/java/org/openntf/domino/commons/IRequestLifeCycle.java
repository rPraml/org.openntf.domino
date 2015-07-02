package org.openntf.domino.commons;

public interface IRequestLifeCycle {

	void beforeRequest(IRequest request);

	void afterRequest();
}
