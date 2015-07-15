package org.openntf.tasklet.impl;

import java.util.concurrent.Callable;

import org.openntf.tasklet.ITaskletWrapper;
import org.openntf.tasklet.ITaskletWrapperFactory;

public class TaskletWrapperFactory implements ITaskletWrapperFactory {

	@Override
	public int getPriority() {
		return 99;
	}

	@Override
	public <RET> ITaskletWrapper<RET> createWrapper(final Runnable runnable, final RET result) {
		return new TaskletWrapper<RET>(runnable, result);
	}

	@Override
	public <RET> ITaskletWrapper<RET> createWrapper(final Callable<RET> callable) {
		return new TaskletWrapper<RET>(callable);
	}

}
