package org.openntf.tasklet;

import java.util.concurrent.Callable;

import org.openntf.domino.commons.IPriority;

public interface ITaskletWrapperFactory extends IPriority {
	public <RET> ITaskletWrapper<RET> createWrapper(final Runnable runnable, final RET result);

	public <RET> ITaskletWrapper<RET> createWrapper(final Callable<RET> callable);
}
