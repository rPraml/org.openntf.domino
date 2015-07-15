package org.openntf.domino.xsp.xots;

import java.util.concurrent.Callable;

import org.openntf.tasklet.ITaskletWrapper;
import org.openntf.tasklet.ITaskletWrapperFactory;

public class XspXotsWrapperFactory implements ITaskletWrapperFactory {

	@Override
	public int getPriority() {
		return 90;
	}

	@Override
	public <RET> ITaskletWrapper<RET> createWrapper(final Runnable runnable, final RET result) {
		return new XspXotsWrapper<RET>(runnable, result);
	}

	@Override
	public <RET> ITaskletWrapper<RET> createWrapper(final Callable<RET> callable) {
		return new XspXotsWrapper<RET>(callable);
	}

}
