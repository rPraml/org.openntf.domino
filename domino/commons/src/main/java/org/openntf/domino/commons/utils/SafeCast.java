package org.openntf.domino.commons.utils;

import org.openntf.domino.commons.exception.DataNotCompatibleException;

public enum SafeCast {
	;

	/**
	 * The valid range of byte = [-128..127]
	 * 
	 * @param l
	 * @return
	 */
	public static byte longToByte(final long l) {
		byte x = (byte) l;
		if (x != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a byte");
		}
		return x;
	}

	/**
	 * The valid range of char = [0..2^16[ = [0..65535]
	 */
	public static char longToChar(final long l) {
		char x = (char) l;
		if (x != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a char");
		}
		return x;
	}

	/**
	 * The valid range of short = [-2^15..2^15[ = [-32768..32767]
	 */
	public static short longToShort(final long l) {
		short x = (short) l;
		if (x != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a short");
		}
		return x;
	}

	/**
	 * The valid range of int is = [-2^31..2^31[ = [-2'147'483'648..2'147'483'647]
	 */
	public static int longToInt(final long l) {
		int x = (int) l;
		if (x != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a int");
		}
		return x;
	}

	/**
	 * The valid range of l = [-2^24..2^24] = [-16'777'216..16'777'216]
	 */
	public static float longToFloat(final long l) {
		float x = l;
		if (Math.abs((long) x - l) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + l + "' gets too inaccurate when cast to float: " + (long) x);
		}
		return x;
	}

	/**
	 * The valid range of l = [-2^53..2^53] = [-9'007'199'254'740'992..9'007'199'254'740'992]
	 */
	public static double longToDouble(final long l) {
		double x = l;
		if (Math.abs((long) x - l) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + l + "' gets too inaccurate when cast to double: " + (long) x);
		}
		return x;
	}

	public static byte doubleToByte(final double d) {
		byte x = (byte) d;
		if (Math.abs(x - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a byte");
		}
		return x;
	}

	public static char doubleToChar(final double d) {
		char x = (char) d;
		if (Math.abs(x - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a char");
		}
		return x;
	}

	public static short doubleToShort(final double d) {
		short x = (short) d;
		if (Math.abs(x - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a short");
		}
		return x;
	}

	public static int doubleToInt(final double d) {
		int x = (int) d;
		if (Math.abs(x - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a int");
		}
		return x;
	}

	public static long doubleToLong(final double d) {
		long x = (long) d;
		if (Math.abs(x - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a long");
		}
		return x;
	}

	public static float doubleToFloat(final double d) {
		if (d < -Float.MAX_VALUE || Float.MAX_VALUE < d) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a float");
		}
		return (float) d;
	}

}
