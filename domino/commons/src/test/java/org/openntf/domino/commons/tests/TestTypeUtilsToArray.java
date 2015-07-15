package org.openntf.domino.commons.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.openntf.domino.commons.utils.TypeUtils.toByte;
import static org.openntf.domino.commons.utils.TypeUtils.toByteArray;
import static org.openntf.domino.commons.utils.TypeUtils.toChar;
import static org.openntf.domino.commons.utils.TypeUtils.toCharArray;
import static org.openntf.domino.commons.utils.TypeUtils.toDouble;
import static org.openntf.domino.commons.utils.TypeUtils.toDoubleArray;
import static org.openntf.domino.commons.utils.TypeUtils.toFloat;
import static org.openntf.domino.commons.utils.TypeUtils.toFloatArray;
import static org.openntf.domino.commons.utils.TypeUtils.toInt;
import static org.openntf.domino.commons.utils.TypeUtils.toIntArray;
import static org.openntf.domino.commons.utils.TypeUtils.toLong;
import static org.openntf.domino.commons.utils.TypeUtils.toLongArray;
import static org.openntf.domino.commons.utils.TypeUtils.toShort;
import static org.openntf.domino.commons.utils.TypeUtils.toShortArray;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.openntf.domino.commons.exception.DataNotCompatibleException;

public class TestTypeUtilsToArray {
	public byte[] byteArr = new byte[] { 42, 7, 3 };
	public Byte[] ByteArr = new Byte[] { 42, 7, 3 };
	public List<Byte> ByteList = Arrays.asList(ByteArr);

	public short[] shortArr = new short[] { 42, 7, 3 };
	public Short[] ShortArr = new Short[] { 42, 7, 3 };
	public List<Short> ShortList = Arrays.asList(ShortArr);

	public int[] intArr = new int[] { 42, 7, 3 };
	public Integer[] IntegerArr = new Integer[] { 42, 7, 3 };
	public List<Integer> IntegerList = Arrays.asList(IntegerArr);

	public long[] longArr = new long[] { 42, 7, 3 };
	public Long[] LongArr = new Long[] { 42L, 7L, 3L };
	public List<Long> LongList = Arrays.asList(LongArr);

	public float[] floatArr = new float[] { 42, 7, 3 };
	public Float[] FloatArr = new Float[] { 42F, 7F, 3F };
	public List<Float> FloatList = Arrays.asList(FloatArr);

	public double[] doubleArr = new double[] { 42, 7, 3 };
	public Double[] DoubleArr = new Double[] { 42D, 7D, 3D };
	public List<Double> DoubleList = Arrays.asList(DoubleArr);

	public char[] charArr = new char[] { 42, 7, 3 };
	public Character[] CharacterArr = new Character[] { 42, 7, 3 };
	public List<Character> CharacterList = Arrays.asList(CharacterArr);

	public String[] textArr = new String[] { "42", "7", "3" };

	public interface Callback {
		public void test(Object o);
	}

	// toByte
	@Test
	public void testToByteArray() {
		Callback cb = new Callback() {
			@Override
			public void test(final Object o) {
				assertArrayEquals(byteArr, toByteArray(o));
			}
		};

		assertArrayEquals(null, toByteArray((Object) null));
		assertArrayEquals(null, toByteArray((List<?>) null));
		assertArrayEquals(null, toByteArray((Collection<?>) null));
		assertArrayEquals(new byte[] { 42 }, toByteArray("42"));
		assertEquals(0, toByte(null));
		assertEquals(0, toByte(""));
		cb.test(textArr);
		testArray(cb);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToByteEx1() {
		toByte("xyz");
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToByteEx2() {
		toByte(getClass());
	}

	// toShort
	@Test
	public void testToShortArray() {
		Callback cb = new Callback() {
			@Override
			public void test(final Object o) {
				assertArrayEquals(shortArr, toShortArray(o));
			}
		};

		assertArrayEquals(null, toShortArray((Object) null));
		assertArrayEquals(null, toShortArray((List<?>) null));
		assertArrayEquals(null, toShortArray((Collection<?>) null));
		assertArrayEquals(new short[] { 42 }, toShortArray("42"));
		assertEquals(0, toShort(null));
		assertEquals(0, toShort(""));
		cb.test(textArr);
		testArray(cb);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToShortEx1() {
		toShort("xyz");
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToShortEx2() {
		toShort(getClass());
	}

	// toInt
	@Test
	public void testToIntArray() {
		Callback cb = new Callback() {
			@Override
			public void test(final Object o) {
				assertArrayEquals(intArr, toIntArray(o));
			}
		};

		assertArrayEquals(null, toIntArray((Object) null));
		assertArrayEquals(null, toIntArray((List<?>) null));
		assertArrayEquals(null, toIntArray((Collection<?>) null));
		assertArrayEquals(new int[] { 42 }, toIntArray("42"));
		assertEquals(0, toInt(null));
		assertEquals(0, toInt(""));
		cb.test(textArr);
		testArray(cb);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToIntEx1() {
		toInt("xyz");
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToIntEx2() {
		toInt(getClass());
	}

	// toLong
	@Test
	public void testToLongArray() {
		Callback cb = new Callback() {
			@Override
			public void test(final Object o) {
				assertArrayEquals(longArr, toLongArray(o));
			}
		};

		assertArrayEquals(null, toLongArray((Object) null));
		assertArrayEquals(null, toLongArray((List<?>) null));
		assertArrayEquals(null, toLongArray((Collection<?>) null));
		assertArrayEquals(new long[] { 42 }, toLongArray("42"));
		assertEquals(0, toLong(null));
		assertEquals(0, toLong(""));
		cb.test(textArr);
		testArray(cb);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToLongEx1() {
		toLong("xyz");
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToLongEx2() {
		toLong(getClass());
	}

	// toFloat
	@Test
	public void testToFloatArray() {
		Callback cb = new Callback() {
			@Override
			public void test(final Object o) {
				assertArrayEquals(floatArr, toFloatArray(o), 0.0001F);
			}
		};

		assertArrayEquals(null, toFloatArray((Object) null), 0.0001F);
		assertArrayEquals(null, toFloatArray((List<?>) null), 0.0001F);
		assertArrayEquals(null, toFloatArray((Collection<?>) null), 0.0001F);
		assertArrayEquals(new float[] { 42 }, toFloatArray("42"), 0.0001F);
		assertEquals(0, toFloat(null), 0.001F);
		assertEquals(0, toFloat(""), 0.001F);
		cb.test(textArr);
		testArray(cb);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToFloatEx1() {
		toFloat("xyz");
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToFloatEx2() {
		toFloat(getClass());
	}

	// toDouble
	@Test
	public void testToDoubleArray() {
		Callback cb = new Callback() {
			@Override
			public void test(final Object o) {
				assertArrayEquals(doubleArr, toDoubleArray(o), 0.0001D);
			}
		};

		assertArrayEquals(null, toDoubleArray((Object) null), 0.0001D);
		assertArrayEquals(null, toDoubleArray((List<?>) null), 0.0001D);
		assertArrayEquals(null, toDoubleArray((Collection<?>) null), 0.0001D);
		assertArrayEquals(new double[] { 42 }, toDoubleArray("42"), 0.0001D);
		assertEquals(0, toDouble(null), 0.001F);
		assertEquals(0, toDouble(""), 0.001F);
		cb.test(textArr);
		testArray(cb);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToDoubleEx1() {
		toDouble("xyz");
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToDoubleEx2() {
		toDouble(getClass());
	}

	// toChar
	@Test
	public void testToCharArray() {
		Callback cb = new Callback() {
			@Override
			public void test(final Object o) {
				assertArrayEquals(charArr, toCharArray(o));
			}
		};

		assertArrayEquals(null, toCharArray((Object) null));
		assertArrayEquals(null, toCharArray((List<?>) null));
		assertArrayEquals(null, toCharArray((Collection<?>) null));
		assertArrayEquals(new char[] { '4' }, toCharArray("42"));
		assertEquals(0, toChar(null));
		assertEquals(0, toChar(""));
		assertArrayEquals(new char[] { '4', '7', '3' }, toCharArray(textArr));
		testArray(cb);
	}

	public void testToCharEx1() {
		assertEquals('x', toChar("xyz"));
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testToCharEx2() {
		toChar(getClass());
	}

	protected void testArray(final Callback cb) {
		cb.test(byteArr);
		cb.test(ByteArr);
		cb.test(ByteList);
		cb.test(Collections.unmodifiableCollection(ByteList));

		cb.test(charArr);
		cb.test(CharacterArr);
		cb.test(CharacterList);
		cb.test(Collections.unmodifiableCollection(CharacterList));

		cb.test(shortArr);
		cb.test(ShortArr);
		cb.test(ShortList);
		cb.test(Collections.unmodifiableCollection(ShortList));

		cb.test(intArr);
		cb.test(IntegerArr);
		cb.test(IntegerList);
		cb.test(Collections.unmodifiableCollection(IntegerList));

		cb.test(longArr);
		cb.test(LongArr);
		cb.test(LongList);
		cb.test(Collections.unmodifiableCollection(LongList));

		cb.test(floatArr);
		cb.test(FloatArr);
		cb.test(FloatList);
		cb.test(Collections.unmodifiableCollection(FloatList));

		cb.test(doubleArr);
		cb.test(DoubleArr);
		cb.test(DoubleList);
		cb.test(Collections.unmodifiableCollection(DoubleList));

	}
}
