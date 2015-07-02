package org.openntf.domino.commons.tests;

import org.junit.Test;
import org.openntf.domino.commons.LifeCycleManager;

public class TestLifeCycle {

	@Test
	public void testLifeCycle() {
		LifeCycleManager.startup();
		LifeCycleManager.shutdown();
	}
}
