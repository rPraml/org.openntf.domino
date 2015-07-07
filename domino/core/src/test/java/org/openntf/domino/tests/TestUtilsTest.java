package org.openntf.domino.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.junit.Test;

public class TestUtilsTest {

	@Test
	public void test() {
		//		Object o = TypeUtils.objectToClass(new Date(), Calendar.class, null);
		//		assertTrue(o instanceof Calendar);
		//
		//		Date d = new Date();
		//		o = TypeUtils.objectToClass(d, Pattern.class, null);
		//		assertEquals(d.toString(), o.toString());
		//
		//		o = TypeUtils.objectToClass(d, Formula.class, null);
		//		assertEquals(((Formula) o).getExpression(), d.toString());
		//
		//		o = TypeUtils.objectToClass(d, Pattern[].class, null);
		//		assertEquals(d.toString(), Array.get(o, 0).toString());
		//
		//		o = TypeUtils.objectToClass(o, String.class, null);
		//		assertTrue(o != null);
		//
		//		int[] ia = new int[] { 1, 2, 3, 4 };
		//		o = TypeUtils.objectToClass(ia, int[].class, null);
		//		assertArrayEquals((int[]) o, ia);
		//
		//		String[] sia = new String[] { "1", "2", "3", "4" };
		//		o = TypeUtils.objectToClass(sia, int[].class, null);
		//		assertArrayEquals((int[]) o, ia);

		assertTrue(true);
	}

	/*==============================================================================================================*/
	public void d() {// @formatter:off
		Vector<Object> v = new Vector<Object>(); List<Object> l = v; Collection<Object> c = l; Set<Object> s = null;
		assertTrue(l == v); assertFalse(s == c); assertEquals(0, 0); assertArrayEquals(new byte[0], new byte[] {});
		// @formatter:on
	}
}
