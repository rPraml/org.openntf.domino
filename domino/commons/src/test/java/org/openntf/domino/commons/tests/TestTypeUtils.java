package org.openntf.domino.commons.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openntf.domino.commons.utils.TypeUtils.objectToClass;
import static org.openntf.domino.commons.utils.TypeUtils.toCollection;
import static org.openntf.domino.commons.utils.TypeUtils.toList;
import static org.openntf.domino.commons.utils.TypeUtils.toVector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.openntf.domino.commons.IName;
import org.openntf.domino.commons.exception.DataNotCompatibleException;
import org.openntf.domino.commons.utils.SafeCast;
import org.openntf.domino.commons.utils.TypeUtils;

import com.ibm.icu.util.Calendar;

public class TestTypeUtils {

	public interface SpecialCollection extends Collection<String> {

	}

	/**
	 * Needed to cover exception handling in TypeUtils
	 */
	public static class PrivateClass {
		public PrivateClass(final String s) {
			throw new UnsupportedOperationException("Class is private");
		}
	}

	public static class SpecialNumber extends Number {
		private int value;

		public SpecialNumber(final CharSequence cs) {
			value = Integer.valueOf(cs.toString());
		}

		@Override
		public double doubleValue() {
			return value;
		}

		@Override
		public float floatValue() {
			return value;
		}

		@Override
		public int intValue() {
			return value;
		}

		@Override
		public long longValue() {
			return value;
		}

	}

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
		short s = 32;
		char ch = (char) s;
		SafeCast.longToChar(s);

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

	public void testHowArraysWork() {

		Double.valueOf("200000000000000000000000000000000000");
		double d = 1E200;
		long l = 12345678;
		short s = (short) l;
		l = (long) d;

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
		//not stable: elements will swap!
		//assertArrayEquals(new String[] { "Foo", "Bar" }, c.toArray());
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
		assertEquals(4, iArr[2]);
		assertEquals(10000, iArr[3]);

		short[] sArr = objectToClass(LONG_NUMBER_ARR, short[].class);
		assertEquals(42, sArr[0]);
		assertEquals(2, sArr[1]);
		assertEquals(4, sArr[2]);
		assertEquals(10000, sArr[3]);
		// 10000 does not fit into byte!
		//		byte[] bArr = objectToClass(LONG_NUMBER_ARR, byte[].class);
		//		assertEquals(42, bArr[0]);
		//		assertEquals(2, bArr[1]);
		//		assertEquals(4, bArr[2]);
		//		assertEquals(10000, bArr[3]);

	}

	protected static <T> Iterable<T> iterable(final Iterable<T> in) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return in.iterator();
			}
		};
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testToVectorList() {
		Vector vec = new Vector();
		vec.add("a");
		vec.add("b");
		vec.add(null);
		List lst = new ArrayList();
		lst.add("a");
		lst.add("b");
		lst.add(null);

		final Collection col = Collections.unmodifiableCollection(vec);

		String[] arr = new String[] { "a", "b", null };

		assertEquals(null, toVector(null));
		assertEquals(vec, toVector(arr));
		assertEquals(vec, toVector(lst));
		assertEquals(vec, toVector(vec));
		assertEquals(vec, toVector(col));
		assertEquals(vec, toVector(iterable(vec)));
		assertEquals(vec, toVector(Collections.enumeration(vec)));
		Vector tmpVec = new Vector();
		tmpVec.add("a");
		assertEquals(tmpVec, toVector("a"));

		// The same for List
		assertEquals(null, toList(null));
		assertEquals(vec, toList(arr));
		assertEquals(vec, toList(lst));
		assertEquals(vec, toList(vec));
		assertEquals(vec, toList(col));
		assertEquals(vec, toList(iterable(vec)));
		assertEquals(vec, toList(Collections.enumeration(vec)));
		List tmpLst = new ArrayList();
		tmpLst.add("a");
		assertEquals(tmpLst, toList("a"));

		assertEquals(null, toCollection(null, ArrayList.class));
		assertEquals(vec, toCollection(arr, ArrayList.class));
		assertEquals(vec, toCollection(lst, ArrayList.class));
		assertEquals(vec, toCollection(vec, ArrayList.class));
		assertEquals(vec, toCollection(col, ArrayList.class));
		assertEquals(vec, toCollection(iterable(vec), ArrayList.class));
		assertEquals(vec, toCollection(Collections.enumeration(vec), ArrayList.class));
		tmpLst = new ArrayList();
		tmpLst.add("a");
		assertEquals(tmpLst, toCollection("a", ArrayList.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToCollectionEx1() {
		toCollection("a", SpecialCollection.class);
	}

	@Test
	public void testObjectToClass() {
		assertEquals(null, objectToClass(null, String.class));
		assertEquals("Hello", objectToClass("Hello", String.class));
		assertEquals("Hello", objectToClass("Hello", CharSequence.class));
		String[] sArr = new String[] { "foo", "bar" };
		CharSequence[] cArr = new CharSequence[] { "foo", "bar" };

		assertArrayEquals(sArr, objectToClass(sArr, String[].class));
		assertArrayEquals(sArr, objectToClass(cArr, String[].class));

		assertArrayEquals(cArr, objectToClass(sArr, CharSequence[].class));
		assertArrayEquals(cArr, objectToClass(cArr, CharSequence[].class));

		assertEquals("1", objectToClass(1, String.class));
		assertEquals("1", objectToClass(1, CharSequence.class));
		List cmp = new ArrayList();
		cmp.add(1);
		assertEquals(cmp, objectToClass(1, Vector.class));
		assertEquals(cmp, objectToClass(1, List.class));

		assertArrayEquals(new byte[] { 1 }, objectToClass(1, byte[].class));
		assertArrayEquals(new short[] { 1 }, objectToClass(1, short[].class));
		assertArrayEquals(new int[] { 1 }, objectToClass(1, int[].class));
		assertArrayEquals(new long[] { 1 }, objectToClass(1, long[].class));
		assertArrayEquals(new float[] { 1 }, objectToClass(1, float[].class), 0.001F);
		assertArrayEquals(new double[] { 1 }, objectToClass(1, double[].class), 0.001F);

		assertArrayEquals(new char[] { 'x' }, objectToClass("x", char[].class));

		boolean[] bArr = objectToClass("true", boolean[].class);
		assertTrue(bArr[0]);
		assertEquals(1, bArr.length);

		// now test special return types:

		Object[] ret = objectToClass("foo", Object[].class);
		assertEquals("foo", ret[0]);
		assertEquals(1, ret.length);
		List<String> lst = Arrays.asList(sArr);
		checkObjectToClassArrayLike(sArr);
		checkObjectToClassArrayLike(lst);
		checkObjectToClassArrayLike(Collections.unmodifiableCollection(lst));
		checkObjectToClassArrayLike(Collections.enumeration(lst));
		checkObjectToClassArrayLike(iterable(lst));

		// Check conversion of primitves (only first value must be returned!:
		lst = new ArrayList<String>();
		assertEquals(null, objectToClass(lst, Integer.class));
		Integer i42 = Integer.valueOf(42);
		Long l42 = Long.valueOf(42);
		Double d42 = Double.valueOf(42);
		List<Integer> ii42 = new ArrayList<Integer>();
		ii42.add(Integer.valueOf(42));
		ii42.add(Integer.valueOf(43));
		List<Long> ll42 = new ArrayList<Long>();
		ll42.add(Long.valueOf(42));
		ll42.add(Long.valueOf(43));
		List<Double> dd42 = new ArrayList<Double>();
		dd42.add(Double.valueOf(42));
		dd42.add(Double.valueOf(43));

		assertEquals(i42, objectToClass(i42, Number.class));
		assertEquals(l42, objectToClass(l42, Number.class));
		assertEquals(d42, objectToClass(d42, Number.class));
		assertEquals(i42, objectToClass(ii42, Number.class));
		assertEquals(l42, objectToClass(ll42, Number.class));
		assertEquals(d42, objectToClass(dd42, Number.class));

		assertEquals(Byte.valueOf((byte) 42), objectToClass(i42, Byte.class));
		assertEquals(Byte.valueOf((byte) 42), objectToClass(l42, Byte.class));
		assertEquals(Byte.valueOf((byte) 42), objectToClass(d42, Byte.class));
		assertEquals(Byte.valueOf((byte) 42), objectToClass(ii42, Byte.class));
		assertEquals(Byte.valueOf((byte) 42), objectToClass(ll42, Byte.class));
		assertEquals(Byte.valueOf((byte) 42), objectToClass(dd42, Byte.class));

		assertEquals(Short.valueOf((short) 42), objectToClass(i42, Short.class));
		assertEquals(Short.valueOf((short) 42), objectToClass(l42, Short.class));
		assertEquals(Short.valueOf((short) 42), objectToClass(d42, Short.class));
		assertEquals(Short.valueOf((short) 42), objectToClass(ii42, Short.class));
		assertEquals(Short.valueOf((short) 42), objectToClass(ll42, Short.class));
		assertEquals(Short.valueOf((short) 42), objectToClass(dd42, Short.class));

		assertEquals(Integer.valueOf(42), objectToClass(i42, Integer.class));
		assertEquals(Integer.valueOf(42), objectToClass(l42, Integer.class));
		assertEquals(Integer.valueOf(42), objectToClass(d42, Integer.class));
		assertEquals(Integer.valueOf(42), objectToClass(ii42, Integer.class));
		assertEquals(Integer.valueOf(42), objectToClass(ll42, Integer.class));
		assertEquals(Integer.valueOf(42), objectToClass(dd42, Integer.class));

		assertEquals(Long.valueOf(42), objectToClass(i42, Long.class));
		assertEquals(Long.valueOf(42), objectToClass(l42, Long.class));
		assertEquals(Long.valueOf(42), objectToClass(d42, Long.class));
		assertEquals(Long.valueOf(42), objectToClass(ii42, Long.class));
		assertEquals(Long.valueOf(42), objectToClass(ll42, Long.class));
		assertEquals(Long.valueOf(42), objectToClass(dd42, Long.class));

		assertEquals(Float.valueOf(42), objectToClass(i42, Float.class));
		assertEquals(Float.valueOf(42), objectToClass(l42, Float.class));
		assertEquals(Float.valueOf(42), objectToClass(d42, Float.class));
		assertEquals(Float.valueOf(42), objectToClass(ii42, Float.class));
		assertEquals(Float.valueOf(42), objectToClass(ll42, Float.class));
		assertEquals(Float.valueOf(42), objectToClass(dd42, Float.class));

		assertEquals(Double.valueOf(42), objectToClass(i42, Double.class));
		assertEquals(Double.valueOf(42), objectToClass(l42, Double.class));
		assertEquals(Double.valueOf(42), objectToClass(d42, Double.class));
		assertEquals(Double.valueOf(42), objectToClass(ii42, Double.class));
		assertEquals(Double.valueOf(42), objectToClass(ll42, Double.class));
		assertEquals(Double.valueOf(42), objectToClass(dd42, Double.class));

		assertEquals(Character.valueOf((char) 42), objectToClass(42, Character.class));
		assertEquals(Boolean.TRUE, objectToClass(42, Boolean.class));

		assertEquals(Boolean.TRUE, objectToClass(42, Boolean.class));

		// RPr: and now test very special things: (@NTF: FOCONIS has no use case for this - does anyone else use this?) 
		Pattern[] p = objectToClass("<a\\b[^>]*href=\"[^>]*>(.*?)</a>", Pattern[].class);
		Matcher m = p[0].matcher("This is the <a href=\"http://www.openntf.org\">OpenNTF Website</a>!");
		assertTrue(m.find());
		assertEquals("<a href=\"http://www.openntf.org\">OpenNTF Website</a>", m.group());

		Class<?> cls = objectToClass("java.lang.String", Class.class);
		assertEquals(String.class, cls);

		// This is also very special, but maybe used by Foconis
		IName name = objectToClass("CN=Roland Praml/OU=01/OU=int/O=FOCONIS", IName.class);
		assertEquals("Roland Praml", name.getCommon());
		assertEquals("Roland Praml/01/int/FOCONIS", name.getAbbreviated());

		// Check Date & Calendar

		Calendar ref = Calendar.getInstance();
		ref.clear();
		ref.setLenient(false);
		ref.set(1979, 7, 17, 0, 0, 0);

		Date d = objectToClass("1979-08-17", Date.class);
		assertEquals(ref.getTime(), d);

		Calendar c = objectToClass("1979-08-17", Calendar.class);
		assertEquals(ref, c);

		// Test Constructor finder:
		SpecialNumber isit42 = objectToClass("42", SpecialNumber.class);
		assertEquals(42, isit42.intValue());
	}

	private void checkObjectToClassArrayLike(final Object arr) {
		Object[] ret = objectToClass(arr, Object[].class);
		assertEquals("foo", ret[0]);
		assertEquals("bar", ret[1]);
		assertEquals(2, ret.length);

	}

	@Test(expected = DataNotCompatibleException.class)
	public void testObjectToClassEx1() {
		objectToClass(1, Byte.TYPE);

	}

	@Test(expected = DataNotCompatibleException.class)
	public void testObjectToClassEx2() {
		objectToClass("java.lang.Stringxx", Class.class);

	}

	@Test(expected = DataNotCompatibleException.class)
	public void testObjectToClassEx3() {
		objectToClass("42", PrivateClass.class);

	}
}
