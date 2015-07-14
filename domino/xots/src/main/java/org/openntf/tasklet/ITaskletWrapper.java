package org.openntf.tasklet;

import java.util.Observer;
import java.util.concurrent.Callable;

public interface ITaskletWrapper<T> extends Callable<T> {

	void stop();

	String getDescription();

	void setObserver(Observer observer);

}