package org.openntf.domino.xots.tasks;

import java.util.concurrent.Callable;

public interface IWrappedCallable<V> extends IWrappedTask, Callable<V> {

}