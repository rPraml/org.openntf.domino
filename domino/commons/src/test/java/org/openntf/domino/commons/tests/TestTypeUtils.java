package org.openntf.domino.commons.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openntf.domino.commons.utils.TypeUtils.objectToClass;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.junit.Test;
import org.openntf.domino.commons.utils.SafeCast;
import org.openntf.domino.commons.utils.TypeUtils;

public class TestTypeUtils {
	public static double DELTA = 0.000001;
	public static double[] DOUBLE_NUMBER_ARR = new double[] { 42, 2, 3.99, 1E4 };
	public static long[] LONG_NUMBER_ARR = new long[] { 42, 2, 4, 10000 };
	public static short[] SHORT_NUMBER_ARR = new short[] { 42, 2, 4, 10000 };

	public static String[] STRING_NUMBER_ARR = new String[] { "42", "2", "3.99", "1E4" };
	public static String[] STRING_ARR = new String[] { "Foo", "Bar", "Bar" };
	public static List<String> STRING_LIST = Arrays.asList(STRING_ARR);

	public static int VALUE_TRUE1 = 1;
	public static int[] VALUE_TRUE2 = new int[] { 42 };
	public static Integer VALUE_TRUE3 = Integer.valueOf(42);
	public static Integer[] VALUE_TRUE4 = new Integer[] { 42 };
	public static Collection VALUE_TRUE5 = Arrays.asList(VALUE_TRUE4);

	public static double VALUE_FALSE1 = 0.000001;
	public static String VALUE_FALSE2 = "0";
	public static String VALUE_TRUE6 = "true";

	private static interface IEnum {
	}

	private enum Enum1 {
		ONE, TWO, THREE
	}

	private enum Enum2 implements IEnum {
		FOUR, FIVE
	}

	@Test
	public void testSomeBooleans() {

		assertTrue(objectToClass(VALUE_TRUE1, Boolean.class));
		assertTrue(objectToClass(VALUE_TRUE2, Boolean.class));
		assertTrue(objectToClass(VALUE_TRUE3, Boolean.class));
		assertTrue(objectToClass(VALUE_TRUE4, Boolean.class));

		assertTrue(objectToClass(VALUE_TRUE1, Boolean[].class)[0]);
		assertTrue(objectToClass(VALUE_TRUE2, Boolean[].class)[0]);
		assertTrue(objectToClass(VALUE_TRUE3, Boolean[].class)[0]);
		assertTrue(objectToClass(VALUE_TRUE4, Boolean[].class)[0]);
		assertTrue(objectToClass(VALUE_TRUE5, Boolean[].class)[0]);

		assertTrue(objectToClass(VALUE_TRUE1, boolean[].class)[0]);
		assertTrue(objectToClass(VALUE_TRUE2, boolean[].class)[0]);
		assertTrue(objectToClass(VALUE_TRUE3, boolean[].class)[0]);
		assertTrue(objectToClass(VALUE_TRUE4, boolean[].class)[0]);
		assertTrue(objectToClass(VALUE_TRUE5, boolean[].class)[0]);
		assertTrue(objectToClass(VALUE_TRUE6, boolean[].class)[0]);

		assertFalse(objectToClass(VALUE_FALSE1, boolean[].class)[0]);
		assertFalse(objectToClass(VALUE_FALSE2, boolean[].class)[0]);

		Object v = objectToClass(VALUE_TRUE5, Boolean.class);
		assertEquals(Boolean.class, v.getClass());

		v = objectToClass(VALUE_TRUE5, Boolean[].class);
		assertEquals(Boolean[].class, v.getClass());

		v = objectToClass(VALUE_TRUE5, boolean[].class);
		assertEquals(boolean[].class, v.getClass());

	}

	@Test
	public void testChar() {
		short s = -1;
		char ch = (char) s;
		SafeCast.longToChar(s);
		System.out.println(ch + 0);

	}

	@Test
	public void testMisc() throws SecurityException, NoSuchMethodException {
		String isoDate = "2015-01-01";

		List<Date> l = new ArrayList<Date>();
		l.add(new Date(123));
		l.add(new Date(234));
		l.add(new Date(456));

		Date.class.getConstructor(long.class);

		assertEquals(123, TypeUtils.objectToClass(l, Date.class).getTime());

		TypeUtils.objectToClass(new Date(123), Date[].class);
		TypeUtils.objectToClass(123, Date[].class);

		TypeUtils.objectToClass(new Date(123), Long[].class);
	}

	@Test
	public void testEnum() {
		assertEquals(Enum1.THREE, objectToClass("THREE", Enum1.class));
		assertEquals(Enum2.FOUR, objectToClass("FOUR", Enum2.class));
		assertArrayEquals(new Enum1[] { Enum1.ONE, Enum1.THREE }, objectToClass(new String[] { "ONE", "THREE" }, Enum1[].class));

	}

	//	@Test
	//	public void testLongToFloat() {
	//		long l = Long.MAX_VALUE;
	//		System.out.println("A" + l);
	//		l = 1;
	//		System.out.println("B" + l);
	//		while ((l = l * 2) > 0) {
	//			System.out.println(SafeCast.longToFloat(l - 1));
	//		}
	//
	//	}
	//
	//	@Test
	//	public void testLongToDouble() {
	//		long l = Long.MAX_VALUE;
	//		System.out.println("A" + l);
	//		l = 1;
	//		System.out.println("B" + l);
	//		while ((l = l * 2) > 0) {
	//			System.out.println(SafeCast.longToDouble(l - 1));
	//		}
	//
	//	}

	@Test
	public void testHowArraysWork() {

		Double.valueOf("200000000000000000000000000000000000");
		double d = 1E200;
		long l = 12345678;
		short s = (short) l;
		l = (long) d;
		System.out.println(l);

		byte[] bArr = new byte[] { 1 };
		short[] sArr = new short[] { 1 };
		int[] iArr = new int[] { 1 };
		long[] lArr = new long[] { 1 };
		float[] fArr = new float[] { 1 };
		double[] dArr = new double[] { 1 };

		assertEquals(1, Array.getByte(bArr, 0));
		assertEquals(1, Array.getShort(bArr, 0));
		assertEquals(1, Array.getInt(bArr, 0));
		assertEquals(1, Array.getLong(bArr, 0));
		assertEquals(1, Array.getFloat(bArr, 0), DELTA);
		assertEquals(1, Array.getDouble(bArr, 0), DELTA);

		//assertEquals(1, Array.getByte(sArr, 0));
		assertEquals(1, Array.getShort(sArr, 0));
		assertEquals(1, Array.getInt(sArr, 0));
		assertEquals(1, Array.getLong(sArr, 0));
		assertEquals(1, Array.getFloat(sArr, 0), DELTA);
		assertEquals(1, Array.getDouble(sArr, 0), DELTA);

		//assertEquals(1, Array.getByte(iArr, 0));
		//assertEquals(1, Array.getShort(iArr, 0));
		assertEquals(1, Array.getInt(iArr, 0));
		assertEquals(1, Array.getLong(iArr, 0));
		assertEquals(1, Array.getFloat(iArr, 0), DELTA);
		assertEquals(1, Array.getDouble(iArr, 0), DELTA);

		//assertEquals(1, Array.getByte(lArr, 0));
		//assertEquals(1, Array.getShort(lArr, 0));
		//assertEquals(1, Array.getInt(lArr, 0));
		assertEquals(1, Array.getLong(lArr, 0));
		assertEquals(1, Array.getFloat(lArr, 0), DELTA);
		assertEquals(1, Array.getDouble(lArr, 0), DELTA);

		//assertEquals(1, Array.getByte(fArr, 0));
		//assertEquals(1, Array.getShort(fArr, 0));
		//assertEquals(1, Array.getInt(fArr, 0));
		//assertEquals(1, Array.getLong(fArr, 0));
		assertEquals(1, Array.getFloat(fArr, 0), DELTA);
		assertEquals(1, Array.getDouble(fArr, 0), DELTA);

		//assertEquals(1, Array.getByte(dArr, 0));
		//assertEquals(1, Array.getShort(dArr, 0));
		//assertEquals(1, Array.getInt(dArr, 0));
		//assertEquals(1, Array.getLong(dArr, 0));
		//assertEquals(1, Array.getFloat(dArr, 0), DELTA);
		assertEquals(1, Array.getDouble(dArr, 0), DELTA);
	}

	@Test
	public void testConversions() {

		Vector v = objectToClass(STRING_ARR, Vector.class);
		assertArrayEquals(STRING_ARR, v.toArray());

		v = objectToClass(STRING_LIST, Vector.class);
		assertArrayEquals(STRING_ARR, v.toArray());

		Collection c = objectToClass(STRING_LIST, List.class);
		assertTrue(c instanceof List);
		assertArrayEquals(STRING_ARR, c.toArray());

		c = objectToClass(STRING_LIST, Collection.class);
		assertTrue(c instanceof Collection);
		assertArrayEquals(STRING_ARR, c.toArray());

		c = objectToClass(STRING_LIST, Set.class);
		assertTrue(c instanceof Set);
		assertArrayEquals(new String[] { "Foo", "Bar" }, c.toArray());
		String s = objectToClass(STRING_LIST, String.class);
		assertEquals("Foo, Bar, Bar", s);

		int i = objectToClass(STRING_NUMBER_ARR, Integer.class);
		assertEquals(42, i);

		double[] dArr = objectToClass(STRING_NUMBER_ARR, double[].class);
		assertEquals(42, dArr[0], DELTA);
		assertEquals(2, dArr[1], DELTA);
		assertEquals(3.99, dArr[2], DELTA);
		assertEquals(10000, dArr[3], DELTA);

		dArr = objectToClass(DOUBLE_NUMBER_ARR, double[].class);
		assertEquals(42, dArr[0], DELTA);
		assertEquals(2, dArr[1], DELTA);
		assertEquals(3.99, dArr[2], DELTA);
		assertEquals(10000, dArr[3], DELTA);

		int[] iArr = objectToClass(LONG_NUMBER_ARR, int[].class);
		assertEquals(42, iArr[0]);
		assertEquals(2, iArr[1]);
		assertEquals(3, iArr[2]);
		assertEquals(4, iArr[3]);

		short[] sArr = objectToClass(LONG_NUMBER_ARR, short[].class);
		assertEquals(42, sArr[0]);
		assertEquals(2, sArr[1]);
		assertEquals(3, sArr[2]);
		assertEquals(4, sArr[3]);

		byte[] bArr = objectToClass(LONG_NUMBER_ARR, byte[].class);
		assertEquals(42, bArr[0]);
		assertEquals(2, bArr[1]);
		assertEquals(3, bArr[2]);
		assertEquals(4, bArr[3]);

	}

}
