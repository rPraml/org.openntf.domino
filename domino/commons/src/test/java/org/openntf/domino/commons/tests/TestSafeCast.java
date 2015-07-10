package org.openntf.domino.commons.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openntf.domino.commons.utils.SafeCast.doubleToByte;
import static org.openntf.domino.commons.utils.SafeCast.doubleToChar;
import static org.openntf.domino.commons.utils.SafeCast.doubleToFloat;
import static org.openntf.domino.commons.utils.SafeCast.doubleToInt;
import static org.openntf.domino.commons.utils.SafeCast.doubleToLong;
import static org.openntf.domino.commons.utils.SafeCast.doubleToShort;
import static org.openntf.domino.commons.utils.SafeCast.longToByte;
import static org.openntf.domino.commons.utils.SafeCast.longToChar;
import static org.openntf.domino.commons.utils.SafeCast.longToDouble;
import static org.openntf.domino.commons.utils.SafeCast.longToFloat;
import static org.openntf.domino.commons.utils.SafeCast.longToInt;
import static org.openntf.domino.commons.utils.SafeCast.longToShort;

import org.junit.Test;
import org.openntf.domino.commons.exception.DataNotCompatibleException;
import org.openntf.domino.commons.utils.TypeUtils;

public class TestSafeCast {

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

	@SuppressWarnings("serial")
	public static class INumber extends Number {
		private int i;

		public INumber(final int w) {
			i = w;
		}

		@Override
		public String toString() {
			return "" + i;
		}

		@Override
		public double doubleValue() {
			return longValue();
		}

		@Override
		public float floatValue() {
			return intValue();
		}

		@Override
		public int intValue() {
			return i;
		}

		@Override
		public long longValue() {
			return i + 111;
		}

		public static Number getInst(final int i) {
			return new INumber(i);
		}
	}

	// Byte
	@Test
	public void testLongToByte() {
		assertEquals(127, longToByte(127L));
		assertEquals(0, longToByte(0));
		assertEquals(-128, longToByte(-128L));
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToByteOv1() {
		assertTrue(true);
		longToByte(128L);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToByteOv2() {
		longToByte(-129L);
	}

	// Char
	@Test
	public void testLongToChar() {
		assertEquals('\uFFFF', longToChar(65535L));
		assertEquals('\u0000', longToChar(0L));
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToCharOv1() {
		longToChar(65536L);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToCharOv2() {
		longToChar(-1L);
	}

	// Short
	@Test
	public void testLongToShort() {
		assertEquals(32767, longToShort(32767L));
		assertEquals(0, longToShort(0));
		assertEquals(-32768, longToShort(-32768L));
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToShortOv1() {
		longToShort(32768L);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToShortOv2() {
		longToShort(-32769L);
	}

	@Test
	public void testLongToInt() {
		assertEquals(2147483647, longToInt(2147483647L));
		assertEquals(0, longToInt(0));
		assertEquals(-2147483648, longToInt(-2147483648L));
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToIntOv1() {
		longToInt(2147483648L);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToIntOv2() {
		longToInt(-2147483649L);
	}

	// Float
	@Test
	public void testLongToFloat() {
		// float can store up to 24 bits
		for (long base = 1; base < 24; base++) {
			for (long test = ((2L << base) - 100L); test <= (2L << base); test++) {
				assertEquals(test, (long) longToFloat(test));
			}
		}
		// Test whole range (takes ~2 sec)
		for (long test = -16777216; test < 16777216; test++) {
			assertEquals(test, (long) longToFloat(test));
		}
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToFloatOv1() {
		longToFloat(16777217);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToFloatOv2() {
		longToFloat(-16777217);
	}

	@Test
	public void testLongToDouble() {
		// Double can store up to 53 bits
		for (long base = 1; base < 53; base++) {
			for (long test = ((2L << base) - 10000L); test <= (2L << base); test++) {
				assertEquals(test, (long) longToDouble(test));
			}
		}

		assertEquals(9007199254740990.0D, longToDouble(9007199254740990L), 0.00001D);
		assertEquals(9007199254740991.0D, longToDouble(9007199254740991L), 0.00001D);
		assertEquals(9007199254740992.0D, longToDouble(9007199254740992L), 0.00001D);

		assertEquals(-9007199254740990.0D, longToDouble(-9007199254740990L), 0.00001D);
		assertEquals(-9007199254740991.0D, longToDouble(-9007199254740991L), 0.00001D);
		assertEquals(-9007199254740992.0D, longToDouble(-9007199254740992L), 0.00001D);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToDoubleOv1() {
		longToDouble(9007199254740993L);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testLongToDoubleOv2() {
		longToDouble(-9007199254740993L);
	}

	@Test
	public void testDoubleToByte() {
		assertEquals(-128, doubleToByte(-128.99D));
		assertEquals(0, doubleToByte(-0.9D));
		assertEquals(0, doubleToByte(0.0D));
		assertEquals(0, doubleToByte(0.5D));
		assertEquals(0, doubleToByte(0.9D));
		assertEquals(1, doubleToByte(1.0D));
		assertEquals(1, doubleToByte(1.5D));
		assertEquals(127, doubleToByte(127.99D));
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToByteOv1() {
		doubleToByte(-129.0D);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToByteOv2() {
		doubleToByte(-129.0D);
	}

	@Test
	public void testDoubleToChar() {
		assertEquals(0, doubleToChar(-0.9D));
		assertEquals(0, doubleToChar(0.0D));
		assertEquals(0, doubleToChar(0.5D));
		assertEquals(0, doubleToChar(0.9D));
		assertEquals(1, doubleToChar(1.0D));
		assertEquals(1, doubleToChar(1.5D));
		assertEquals(65535, doubleToChar(65535.99D));
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToCharOv1() {
		doubleToChar(-1.0D);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToCharOv2() {
		doubleToChar(65536.0D);
	}

	@Test
	public void testDoubleToShort() {
		assertEquals(-32768, doubleToShort(-32768.99D));
		assertEquals(0, doubleToShort(-0.9D));
		assertEquals(0, doubleToShort(0.0D));
		assertEquals(0, doubleToShort(0.5D));
		assertEquals(0, doubleToShort(0.9D));
		assertEquals(1, doubleToShort(1.0D));
		assertEquals(1, doubleToShort(1.5D));
		assertEquals(32767, doubleToShort(32767.99D));
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToShortOv1() {
		doubleToShort(-32769.0D);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToShortOv2() {
		doubleToShort(32768.0D);
	}

	@Test
	public void testDoubleToInt() {
		assertEquals(-2147483648, doubleToInt(-2147483648.99D));
		assertEquals(0, doubleToInt(-0.9D));
		assertEquals(0, doubleToInt(0.0D));
		assertEquals(0, doubleToInt(0.5D));
		assertEquals(0, doubleToInt(0.9D));
		assertEquals(1, doubleToInt(1.0D));
		assertEquals(1, doubleToInt(1.5D));
		assertEquals(2147483647, doubleToInt(2147483647.99D));
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToIntOv1() {
		doubleToInt(-2147483649.0D);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToIntOv2() {
		doubleToInt(2147483648.0D);
	}

	@Test
	public void testDoubleToLong() {
		assertEquals(-9223372036854775808L, doubleToLong(-9223372036854775808.99D));
		assertEquals(0, doubleToLong(-0.9D));
		assertEquals(0, doubleToLong(0.0D));
		assertEquals(0, doubleToLong(0.5D));
		assertEquals(0, doubleToLong(0.9D));
		assertEquals(1, doubleToLong(1.0D));
		assertEquals(1, doubleToLong(1.5D));
		assertEquals(9223372036854775807L, doubleToLong(9223372036854775808.99D));
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToLongOv1() {
		doubleToLong(10000000000000000000D);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToLongOv2() {
		doubleToLong(-10000000000000000000D);
	}

	@Test
	public void testDoubleToFloat() {
		//assertEquals(Float.MIN_VALUE, doubleToFloat(Float.MIN_VALUE), 0.0001D);
		assertEquals(-10.0F, doubleToFloat(-10D), 0.0001D);
		assertEquals(-0.9F, doubleToFloat(-0.9D), 0.0001D);
		assertEquals(0F, doubleToFloat(0.0D), 0.0001D);
		assertEquals(0.5F, doubleToFloat(0.5D), 0.0001D);
		assertEquals(0.9F, doubleToFloat(0.9D), 0.0001D);
		assertEquals(1.0F, doubleToFloat(1.0D), 0.0001D);
		assertEquals(1.5D, doubleToFloat(1.5D), 0.0001D);
		assertEquals(Float.MAX_VALUE, doubleToFloat(Float.MAX_VALUE), 0.0001D);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToFloatOv1() {
		doubleToFloat(3.4028236E38);
	}

	@Test(expected = DataNotCompatibleException.class)
	public void testDoubleToFloatOv2() {
		doubleToFloat(-3.4028236E38);
	}

}
