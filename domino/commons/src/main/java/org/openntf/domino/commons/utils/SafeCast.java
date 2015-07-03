package org.openntf.domino.commons.utils;

import org.openntf.domino.commons.exception.DataNotCompatibleException;

public enum SafeCast {
	;

	public static byte longToByte(final long l) {
		byte x = (byte) l;
		if (x != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a byte");
		}
		return x;
	}

	public static char longToChar(final long l) {
		char x = (char) l;
		if (x != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a char");
		}
		return x;
	}

	public static short longToShort(final long l) {
		short x = (short) l;
		if (x != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a short");
		}
		return x;
	}

	public static int longToInt(final long l) {
		int x = (int) l;
		if (x != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a int");
		}
		return x;
	}

	public static float longToFloat(final long l) {
		float x = l;
		if (Math.abs((long) x - l) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + l + "' gets too inaccurate when cast to float: " + (long) x);
		}
		return x;
	}

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
		float x = (float) d;
		if (Math.abs(x - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a float or gets too inaccurate");
		}
		return x;
	}
}
