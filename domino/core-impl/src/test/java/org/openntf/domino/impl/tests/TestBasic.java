package org.openntf.domino.impl.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openntf.domino.junit.DominoJUnitRunner;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

@RunWith(DominoJUnitRunner.class)
public class TestBasic {

	@Test
	public void testSession() {
		assertEquals("CN=junit/O=CI-DUMMY", Factory.getSession(SessionType.CURRENT).getUserName());
	}

}
