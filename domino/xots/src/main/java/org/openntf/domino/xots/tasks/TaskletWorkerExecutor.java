package org.openntf.domino.xots.tasks;

import org.openntf.domino.commons.IPriority;

public interface TaskletWorkerExecutor<T> extends WorkerExecutor<T>, IPriority {

	public void setTaskletWorker(Class<? extends TaskletWorker<T>> clazz, final Object... args);

}