package org.openntf.domino.xots.tasks;

public interface WorkerExecutor<T> {

	public void send(T t);

}
