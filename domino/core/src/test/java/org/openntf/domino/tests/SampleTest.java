package org.openntf.domino.tests;

import org.junit.Test;
import org.openntf.domino.utils.Factory.SessionType;

//@RunWith(DominoJUnitRunner.class)
public class SampleTest {

	@Test
	public void test() {
		// OK
		System.out.println(SessionType.CURRENT.get());
	}

}
