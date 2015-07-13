package org.openntf.domino.xots.tasks.impl;

import java.lang.reflect.Constructor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import org.openntf.domino.commons.IPriority;
import org.openntf.domino.xots.Xots;
import org.openntf.domino.xots.tasks.AbstractWrappedTask;
import org.openntf.domino.xots.tasks.IWrappedCallable;
import org.openntf.domino.xots.tasks.TaskletWorker;
import org.openntf.domino.xots.tasks.TaskletWorkerExecutor;

public class XotsTaskletWorkerExecutor<T> extends AbstractWrappedTask implements TaskletWorkerExecutor<T>, IWrappedCallable<Object>,
IPriority {
	BlockingQueue<T> q = new LinkedBlockingQueue<T>();
	private static final int NOT_RUNNING = 0;
	private static final int RUNNING = 1;

	//private static final int SHUTDOWN = 2;

	private class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 1L;

		public void add(final T t) {
			q.add(t);
			start();
		}

		public void stop() {
			for (;;) {
				int i = getState();
				if (i == NOT_RUNNING)
					return;
				if (i == RUNNING) {
					if (compareAndSetState(i, NOT_RUNNING)) {
						return;
					}
				}
			}
		}

		// NOT_RUNNING => RUNNING
		public void start() {
			for (;;) {
				int i = getState();
				if (i == RUNNING)
					return;
				if (i == NOT_RUNNING) {
					if (compareAndSetState(i, RUNNING)) {
						Xots.getService().submit(XotsTaskletWorkerExecutor.this);
						return;
					}
				}
			}
		}

	}

	private T currentElement;
	private Sync inner = new Sync();

	public XotsTaskletWorkerExecutor() {

	}

	public XotsTaskletWorkerExecutor(final Class<? extends TaskletWorker<T>> clazz, final Object... args) {
		setTaskletWorker(clazz, args);
	}

	@Override
	public void setTaskletWorker(final Class<? extends TaskletWorker<T>> clazz, final Object... args) {
		Constructor<?> ctor = findConstructor(clazz, args);
		try {
			setWrappedTask(ctor.newInstance(args));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected synchronized void setWrappedTask(final Object task) {
		wrappedTask = task;
	}

	@Override
	public void send(final T t) {
		inner.add(t);
	}

	@Override
	public Object call() throws Exception {
		return callOrRun();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Object invokeWrappedTask() throws Exception {
		currentElement = q.poll(2000, TimeUnit.MILLISECONDS);

		if (currentElement != null) {
			TaskletWorker worker = (TaskletWorker) wrappedTask;
			worker.startUp();
			do {
				worker.process(currentElement);
				currentElement = q.poll(2000, TimeUnit.MILLISECONDS);
			} while (currentElement != null);
			worker.tearDown();
		}
		return null;
	}

	@Override
	protected Object callOrRun() throws Exception {
		try {
			return invokeWrappedTask();
		} finally {
			inner.stop();
		}
	}

	@Override
	public int getPriority() {
		return 99;
	}
}