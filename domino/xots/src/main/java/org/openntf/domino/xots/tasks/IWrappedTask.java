package org.openntf.domino.xots.tasks;

import java.util.Observer;

public interface IWrappedTask {

	void addObserver(Observer o);

	void stop();

	String getDescription();

	public Object getWrappedTask();	// Callable<V> or Runnable

}
