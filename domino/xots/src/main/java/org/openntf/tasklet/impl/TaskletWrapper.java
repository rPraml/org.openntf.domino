package org.openntf.tasklet.impl;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.LifeCycleManager;
import org.openntf.domino.commons.utils.ThreadUtils;
import org.openntf.tasklet.ITaskletWrapper;
import org.openntf.tasklet.Tasklet;
import org.openntf.tasklet.Tasklet.Scope;

public class TaskletWrapper<T> implements ITaskletWrapper<T> {

	protected IRequest request;
	protected Callable<T> callable;
	protected Object wrappedObject;
	protected ClassLoader classLoader;
	protected Scope scope;

	public TaskletWrapper(final Callable<T> callable) {
		this.callable = callable;
		init(callable);

	}

	public TaskletWrapper(final Runnable runnable, final T ret) {
		this.callable = Executors.callable(runnable, ret);
		init(runnable);
	}

	protected void init(final Object task) {
		this.wrappedObject = task;
		IRequest req = LifeCycleManager.getCurrentRequest();
		if (req != null) {
			this.request = req.clone("Tasklet: " + task.getClass().getName());
		} else {
			throw new IllegalStateException("Tasklet submitted while no request was in progress");
		}
		this.classLoader = ThreadUtils.getContextClassLoader();
		if (task instanceof Tasklet.Interface) {
			scope = ((Tasklet.Interface) task).getScope();
		}
		if (scope == null) {
			Tasklet annot = task.getClass().getAnnotation(Tasklet.class);
			if (annot != null) {
				scope = annot.scope();
			}
		}
	}

	@Override
	public T call() throws Exception {
		ClassLoader oldCl = ThreadUtils.setContextClassLoader(classLoader);
		try {
			try {
				LifeCycleManager.beforeRequest(request);
				beforeCall();
				return callable.call();
			} finally {
				LifeCycleManager.afterRequest();
			}
		} finally {
			ThreadUtils.setContextClassLoader(oldCl);
		}
	}

	protected void beforeCall() {
	}

	@Override
	public void stop() {
		if (wrappedObject instanceof Tasklet.Interface) {
			((Tasklet.Interface) wrappedObject).stop();
		}
	}

	@Override
	public String getDescription() {
		if (wrappedObject instanceof Tasklet.Interface) {
			return ((Tasklet.Interface) wrappedObject).getDescription();
		}
		return wrappedObject.getClass().getName();
	}

	@Override
	public void setObserver(final Observer observer) {
		if (wrappedObject instanceof Observable) {
			((Observable) wrappedObject).addObserver(observer);
		}
	}

}
