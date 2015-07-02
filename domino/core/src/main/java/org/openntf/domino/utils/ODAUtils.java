package org.openntf.domino.utils;

import java.io.Serializable;
import java.util.Date;

import org.openntf.domino.Session;
import org.openntf.domino.WrapperFactory;
import org.openntf.domino.commons.Hash;
import org.openntf.domino.commons.Strings;
import org.openntf.domino.commons.exception.IExceptionDetails;
import org.openntf.domino.exceptions.OpenNTFNotesException;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.utils.Factory.ThreadConfig;

public enum ODAUtils {
	;

	/**
	 * Handle exception.
	 * 
	 * @param t
	 *            the t
	 * @return the throwable
	 */
	public static Throwable handleException(final Throwable t) {
		return (handleException(t, null, null));
	}

	public static Throwable handleException(final Throwable t, final IExceptionDetails hed) {
		return handleException(t, hed, null);
	}

	public static Throwable handleException(final Throwable t, final IExceptionDetails hed, final String details) {
		if (t instanceof OpenNTFNotesException) {
			OpenNTFNotesException ne = (OpenNTFNotesException) t;
			ne.addExceptionDetails(hed);
			throw ne;
		}
		ThreadConfig tc = Factory.getThreadConfig();
		if (tc == null || tc.bubbleExceptions) {
			throw new OpenNTFNotesException(details, t, hed);
		}
		try {
			// TODO TODO fix this!
			//			AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
			//				@Override
			//				public Object run() throws Exception {
			//					if (log_.getLevel() == null) {
			//						LogUtils.loadLoggerConfig(false, "");
			//					}
			//					if (log_.getLevel() == null) {
			//						log_.setLevel(Level.WARNING);
			//					}
			//					return null;
			//				}
			//			});
			//			AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
			//				@Override
			//				public Object run() throws Exception {
			//					if (LogUtils.hasAccessException(log_)) {
			//						logBackup_.log(Level.SEVERE, t.getLocalizedMessage(), t);
			//					} else {
			//						log_.log(Level.WARNING, t.getLocalizedMessage(), t);
			//					}
			//					return null;
			//				}
			//			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return t;
	}

	public static Throwable handleException(final Throwable t, final String details) {
		return handleException(t, null, details);
	}

	/**
	 * To java date safe. Recycles the date immediately
	 */
	public static Date toJavaDateSafe(final lotus.domino.DateTime dt) {
		Date date = null;
		if (dt != null) {
			if (dt instanceof org.openntf.domino.DateTime) {
				date = ((org.openntf.domino.DateTime) dt).toJavaDate(); // no need to recycle 'cause it's not toxic
			} else {
				WrapperFactory wf = Factory.getWrapperFactory();
				try {
					date = dt.toJavaDate();
				} catch (Throwable t) {
					t.printStackTrace();
				} finally {
					wf.recycle(dt);
				}
			}
		}
		return date;
	}

	// ---- Special String stuff
	/**
	 * Checks if is replica id.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is 16-character hexadecimal sequence
	 */
	public static boolean isReplicaId(final CharSequence value) {
		if (value.length() != 16)
			return false;
		return Strings.isHex(value);
	}

	/**
	 * Checks if is unid.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is 32-character hexadecimal sequence
	 */
	public static boolean isUnid(final CharSequence value) {
		if (value.length() != 32)
			return false;
		return Strings.isHex(value);
	}

	/**
	 * To unid.
	 * 
	 * @param value
	 *            the value
	 * @return a 32-character hexadecimal string that can be used as a UNID, uniquely and deterministically based on the value argument
	 */
	public static String toUnid(final Serializable value) {
		if (value instanceof CharSequence && isUnid((CharSequence) value))
			return value.toString();
		String hash = Hash.md5(value);

		while (hash.length() < 32) {
			hash = "0" + hash;
		}
		return hash.toUpperCase();
	}

	/**
	 * Gets the domino ini var.
	 * 
	 * @param propertyName
	 *            String property to retrieve from notes.ini
	 * @param defaultValue
	 *            String default to use if property is not found
	 * @return String return value from the notes.ini
	 */
	public static String getDominoIniVar(final String propertyName, final String defaultValue) {
		Session sess = Factory.getSession(SessionType.CURRENT);
		if (sess == null)
			return defaultValue;
		String newVal = sess.getEnvironmentString(propertyName, true);
		if (!"".equals(newVal)) {
			return newVal;
		} else {
			return defaultValue;
		}
	}
}
