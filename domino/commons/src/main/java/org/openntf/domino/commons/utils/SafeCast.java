package org.openntf.domino.commons.utils;

import org.openntf.domino.commons.exception.DataNotCompatibleException;

public enum SafeCast {
	;

	public static byte longToByte(final long l) {
		byte b = (byte) l;
		if (b != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a byte");
		}
		return b;
	}

	public static char longToChar(final long l) {
		char s = (char) l;
		if (s != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a char");
		}
		return s;
	}

	public static short longToShort(final long l) {
		short s = (short) l;
		if (s != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a short");
		}
		return s;
	}

	public static int longToInt(final long l) {
		int i = (int) l;
		if (i != l) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a int");
		}
		return i;
	}

	public static float longToFloat(final long l) {
		float f = l;
		if (Math.abs((long) f - l) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a float: " + (long) f);
		}
		return f;
	}

	public static double longToDouble(final long l) {
		double d = l;
		if (Math.abs((long) d - l) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + l + "' does not fit in a double");
		}
		return d;
	}

	public static byte doubleToByte(final double d) {
		byte l = (byte) d;
		if (Math.abs(l - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a byte");
		}
		return l;
	}

	public static char doubleToChar(final double d) {
		char l = (char) d;
		if (Math.abs(l - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a char");
		}
		return l;
	}

	public static short doubleToShort(final double d) {
		short l = (short) d;
		if (Math.abs(l - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a short");
		}
		return l;
	}

	public static int doubleToInt(final double d) {
		int l = (int) d;
		if (Math.abs(l - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a int");
		}
		return l;
	}

	public static long doubleToLong(final double d) {
		long l = (long) d;
		if (Math.abs(l - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a long");
		}
		return l;
	}

	public static float doubleToFloat(final double d) {
		float f = (float) d;
		if (Math.abs(f - d) >= 1.0) {
			throw new DataNotCompatibleException("The value '" + d + "' does not fit in a float");
		}
		return f;
	}
}
