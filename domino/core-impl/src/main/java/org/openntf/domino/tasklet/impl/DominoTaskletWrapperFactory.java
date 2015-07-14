package org.openntf.domino.tasklet.impl;

import java.util.concurrent.Callable;

import org.openntf.tasklet.ITaskletWrapper;
import org.openntf.tasklet.ITaskletWrapperFactory;

public class DominoTaskletWrapperFactory implements ITaskletWrapperFactory {

	@Override
	public int getPriority() {
		return 80;
	}

	@Override
	public <RET> ITaskletWrapper<RET> createWrapper(final Runnable runnable, final RET result) {
		return new DominoTaskletWrapper<RET>(runnable, result);
	}

	@Override
	public <RET> ITaskletWrapper<RET> createWrapper(final Callable<RET> callable) {
		return new DominoTaskletWrapper<RET>(callable);
	}

}
