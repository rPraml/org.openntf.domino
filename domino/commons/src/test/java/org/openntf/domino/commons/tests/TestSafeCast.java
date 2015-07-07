package org.openntf.domino.commons.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.junit.Test;
import org.openntf.domino.commons.exception.DataNotCompatibleException;
import org.openntf.domino.commons.utils.SafeCast;
import org.openntf.domino.commons.utils.TypeUtils;

public class TestSafeCast {

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleTo() {
		SafeCast.doubleToLong(SafeCast.doubleToFloat(1.1));
		double d = 92233720.36854775807D * 100000000000D;
		long l = SafeCast.doubleToLong(d);
		assertTrue(l > 92233720L);
		Float f = SafeCast.doubleToFloat(d);
		assertTrue(f > 92233720F);
	}

	@Test
	// (expected = NumberFormatException.class)
	public void testNumber() {
		Number n = INumber.getInst(11213);
		int i = TypeUtils.objectToClass(n, Integer.class);
		long l = TypeUtils.objectToClass(n, Long.class);
		assertTrue(l == n.longValue());
		assertFalse(i == n.intValue());
		i = Integer.parseInt("٩٩٣٣");
		System.out.println("٩٩٣٣=" + i);
		i = Integer.parseInt("٩٩٣٣103٩٣");
		System.out.println("٩٩٣٣103٩٣=" + i);
	}

	/*==============================================================================================================*/
	public void d() {// @formatter:off
		Vector<Object> v = new Vector<Object>(); List<Object> l = v; Collection<Object> c = l; Set<Object> s = null;
		assertTrue(l == v); assertFalse(s == c); assertEquals(0, 0); assertArrayEquals(new byte[0], new byte[] {});
	}

	@SuppressWarnings("serial")
	public static class INumber extends Number {private int i;public INumber(final int w) {i = w;}@Override public String toString() {return "" + i;}
	@Override public double doubleValue() {return longValue();}@Override public float floatValue() {return intValue();}
	@Override public int intValue() {return i;}@Override public long longValue() {return i + 111;}public static Number getInst(final int i){return new INumber(i);}
	}
	// @formatter:on
}
