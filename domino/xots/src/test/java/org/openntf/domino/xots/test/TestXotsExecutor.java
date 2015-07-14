package org.openntf.domino.xots.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;
import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.LifeCycleManager;
import org.openntf.domino.commons.impl.RequestImpl;
import org.openntf.tasklet.TaskletExecutor;

public class TestXotsExecutor {

	public class TestCall implements Callable<Integer> {

		@Override
		public Integer call() throws Exception {
			return 42;
		}

	}

	@Test
	public void testXotsExecutor() throws InterruptedException, ExecutionException {
		TaskletExecutor xe = new TaskletExecutor(10, 50, "Xots");
		LifeCycleManager.startup();

		IRequest request = new RequestImpl("TestAction");

		LifeCycleManager.beforeRequest(request);
		Future<Integer> f = xe.submit(new TestCall());
		LifeCycleManager.afterRequest();

		assertEquals(Integer.valueOf(42), f.get());
		xe.shutdown();
		LifeCycleManager.shutdown();
	}
}
