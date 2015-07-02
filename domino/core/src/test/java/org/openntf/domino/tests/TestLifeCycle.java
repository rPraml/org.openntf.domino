package org.openntf.domino.tests;

import org.junit.Test;
import org.openntf.domino.commons.LifeCycleManager;

public class TestLifeCycle {

	@Test
	public void testLifeCycle() {
		LifeCycleManager.startup();
		LifeCycleManager.shutdown();
	}
}
