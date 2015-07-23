package org.openntf.domino.commons.utils;

/**
 * Basic routines for measuring elapsed times.
 * 
 * @author Steinsiek
 */
public enum SimpleTimeKeeper {// @formatter:off
	;
	private static class Meas {
		long _init, _prev, _last;
	}
	private static ThreadLocal<Meas> _meas = new ThreadLocal<Meas>() {
		@Override protected Meas initialValue() { return new Meas(); }
	};
	/** Reset for new measuring */
	public static void reinit() { Meas m = _meas.get(); m._init = m._prev = m._last = System.nanoTime(); }
	/** Get elapsed time (in µs) since last reinit */
	public static long getTimeDiff2Init() { return getTimeDiff2InitNano() / 1000; }
	/** Get elapsed time (in µs) since last get */
	public static long getTimeDiff2Prev() { return getTimeDiff2PrevNano() / 1000; }
	/** Get elapsed time (in ns) since last reinit */
	public static long getTimeDiff2InitNano() { Meas m = actualize(); return m._last - m._init; }
	/** Get elapsed time (in ns) since last get */
	public static long getTimeDiff2PrevNano() { Meas m = actualize(); return m._last - m._prev; }

	private static Meas actualize() { Meas m = _meas.get(); m._prev = m._last; m._last = System.nanoTime(); return m; }
}
