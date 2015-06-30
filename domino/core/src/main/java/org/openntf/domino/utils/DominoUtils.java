/*
 * Copyright 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package org.openntf.domino.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openntf.domino.DateTime;
import org.openntf.domino.Item;
import org.openntf.domino.Name;
import org.openntf.domino.Session;
import org.openntf.domino.WrapperFactory;
import org.openntf.domino.commons.Hash;
import org.openntf.domino.commons.Names;
import org.openntf.domino.commons.Strings;
import org.openntf.domino.commons.exception.IExceptionDetails;
import org.openntf.domino.exceptions.InvalidNotesUrlException;
import org.openntf.domino.exceptions.OpenNTFNotesException;
import org.openntf.domino.utils.Factory.SessionType;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

/**
 * The Enum DominoUtils.
 */
public enum DominoUtils {
	;

	/** The Constant log_. */
	private final static Logger log_ = Logger.getLogger("org.openntf.domino");

	/** The Constant logBackup_. */
	private final static Logger logBackup_ = Logger.getLogger("com.ibm.xsp.domino");

	/**
	 * Gets a class from the current contextClassLoader
	 * 
	 * @param className
	 *            the className
	 * @return the class
	 */
	public static Class<?> getClass(final CharSequence className) {
		Class<?> result = null;
		try {
			result = AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
				@Override
				public Class<?> run() throws Exception {
					Class<?> result = null;
					ClassLoader cl = Thread.currentThread().getContextClassLoader();
					try {
						result = Class.forName(className.toString(), false, cl);
					} catch (Throwable t) {
						System.err.println("Got a " + t.getClass() + " trying to load " + className + " from a " + cl.getClass().getName());
						ClassLoader parent = cl.getParent();
						while (null != parent) {
							System.err.println("Parent ClassLoader: " + parent.getClass().getName());
							parent = parent.getParent();
						}
						throw new RuntimeException(t);
					}
					return result;
				}
			});
		} catch (AccessControlException e) {
			e.printStackTrace();
		} catch (PrivilegedActionException e) {
			e.printStackTrace();
		}
		if (result == null) {
			log_.log(Level.WARNING, "Unable to resolve class " + className + " Please check logs for more details.");
		}
		return result;
	}

	private static ThreadLocal<Boolean> bubbleExceptions_ = new ThreadLocal<Boolean>() {

		@Override
		protected Boolean initialValue() {
			System.out.println("INIT");
			return Boolean.valueOf(Factory.getThreadConfig().bubbleExceptions);
		}
	};

	/**
	 * Returns the current bubbleException setting
	 * 
	 * @return true or false
	 */
	public static Boolean getBubbleExceptions() {
		Boolean ret = bubbleExceptions_.get();
		if (ret == null) {
			ret = Boolean.valueOf(Factory.getThreadConfig().bubbleExceptions);
			bubbleExceptions_.set(ret);
		}

		return ret;
	}

	/**
	 * Sets the current exception handling
	 * 
	 * @param value
	 *            True,False or null. Null sets the bubbleExceptionHandling to the application default
	 */
	public static void setBubbleExceptions(final Boolean value) {
		bubbleExceptions_.set(value);
	}

	public static class LoaderObjectInputStream extends ObjectInputStream {
		//		private final ClassLoader loader_;

		public LoaderObjectInputStream(final InputStream in) throws IOException {
			super(in);
			//			loader_ = null;
		}

		//		public LoaderObjectInputStream(final ClassLoader classLoader, final InputStream in) throws IOException {
		//			super(in);
		//			loader_ = classLoader;
		//		}

		@Override
		protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			String name = desc.getName();
			Class<?> result = null;
			try {
				result = DominoUtils.getClass(name);
			} catch (Exception e) {
				result = super.resolveClass(desc);
			}
			if (result == null) {
				result = super.resolveClass(desc);
			}
			return result;
		}
	}

	/**
	 * Checks if is number.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is number
	 * @deprecated Use {@link Strings#isNumber(CharSequence)}
	 */
	@Deprecated
	public static boolean isNumber(final CharSequence value) {
		return Strings.isNumber(value);
	}

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

	public static Throwable handleException(final Throwable t, final String details) {
		return handleException(t, null, details);
	}

	public static Throwable handleException(final Throwable t, final IExceptionDetails hed, final String details) {
		if (t instanceof OpenNTFNotesException) {
			OpenNTFNotesException ne = (OpenNTFNotesException) t;
			ne.addExceptionDetails(hed);
			throw ne;
		}
		if (getBubbleExceptions()) {
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

	/**
	 * Gets the unid from notes url.
	 * 
	 * @param notesurl
	 *            the notesurl
	 * @return the unid from notes url
	 */
	public static String getUnidFromNotesUrl(final CharSequence notesurl) {
		String result = null;
		String trimmed = notesurl.toString().toLowerCase().trim();
		if (trimmed.startsWith("notes://")) {
			int arg = trimmed.lastIndexOf('?');
			if (arg == -1) { // there's no ? so we'll just start from the end
				String chk = trimmed.substring(trimmed.length() - 32, trimmed.length());
				if (isUnid(chk)) {
					result = chk;
				} else {
					System.out.println("Not a unid. We got " + chk);
				}
			} else {
				String chk = trimmed.substring(0, arg);
				chk = chk.substring(chk.length() - 32, chk.length());
				// String chk = trimmed.substring(trimmed.length() - 32 - (arg + 1), trimmed.length() - (arg + 1));
				if (isUnid(chk)) {
					result = chk;
				} else {
					System.out.println("Not a unid. We got " + chk);
				}
			}
		} else {
			throw new InvalidNotesUrlException(notesurl.toString());
		}

		return result;
	}

	/**
	 * Incinerate.
	 * 
	 * @param args
	 *            the args
	 * @deprecated you should recycle objects by passing them to the WrapperFactory
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Deprecated
	public static void incinerate(final Object... args) {
		for (Object o : args) {
			if (o != null) {
				if (o instanceof lotus.domino.Base) {
					// try {
					// ((Base) o).recycle();
					// } catch (Throwable t) {
					// // who cares?
					// }
					WrapperFactory wf = Factory.getWrapperFactory();
					wf.recycle((lotus.domino.Base) o);
				} else if (o instanceof Map) {
					Set<Map.Entry> entries = ((Map) o).entrySet();
					for (Map.Entry<?, ?> entry : entries) {
						incinerate(entry.getKey(), entry.getValue());
					}
				} else if (o instanceof Collection) {
					Iterator i = ((Collection) o).iterator();
					while (i.hasNext()) {
						Object obj = i.next();
						incinerate(obj);
					}

				} else if (o.getClass().isArray()) {
					try {
						Object[] objs = (Object[]) o;
						for (Object ao : objs) {
							incinerate(ao);
						}
					} catch (Throwable t) {
						// who cares?
					}
				}
			}
		}
	}

	/**
	 * @deprecated Roland Praml: Use the {@link org.openntf.domino.commons.Names} instead
	 */
	@Deprecated
	public static boolean isHierarchicalName(final CharSequence name) {
		return Names.parse(name).isHierarchical();
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
	 * To unid.
	 * 
	 * @param value
	 *            the value
	 * @return a 32-character hexadecimal string that can be used as a UNID, uniquely and deterministically based on the value argument
	 */
	public static String toUnid(final Serializable value) {
		if (value instanceof CharSequence && DominoUtils.isUnid((CharSequence) value))
			return value.toString();
		String hash = Hash.md5(value);

		while (hash.length() < 32) {
			hash = "0" + hash;
		}
		return hash.toUpperCase();
	}

	public static byte[] toByteArray(final CharSequence hexString) {
		if (hexString.length() % 2 != 0)
			throw new IllegalArgumentException("Only hex strings with an even number of digits can be converted");
		int arrLength = hexString.length() >> 1;
		byte buf[] = new byte[arrLength];

		for (int ii = 0; ii < arrLength; ii++) {
			int index = ii << 1;

			CharSequence l_digit = hexString.subSequence(index, index + 2);
			buf[ii] = (byte) Integer.parseInt(l_digit.toString(), 16);
		}
		return buf;
	}

	public static String toHex(final byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static Integer toInteger(final byte[] bytes) {
		//FIXME NTF This feels wrong. Should we pad the byte array? Am I being pedantic?
		if (bytes.length == 1) {
			return Byte.valueOf(bytes[0]).intValue();
		} else if (bytes.length == 2) {
			ByteBuffer wrapped = ByteBuffer.wrap(bytes);
			short s = wrapped.getShort();
			return Short.valueOf(s).intValue();
		} else if (bytes.length == 4) {
			ByteBuffer wrapped = ByteBuffer.wrap(bytes);
			int i = wrapped.getInt();
			return i;
		} else {
			throw new IllegalArgumentException("Cannot convert a byte array of length " + bytes.length + " to Integer");
		}
	}

	/**
	 * To java calendar safe.
	 * 
	 * @param dt
	 *            the dt
	 * @return the calendar
	 */
	public static Calendar toJavaCalendarSafe(final lotus.domino.DateTime dt) {
		Date d = DominoUtils.toJavaDateSafe(dt);
		Calendar c = Calendar.getInstance(ULocale.getDefault());
		c.setTime(d);
		return c;
	}

	/**
	 * To java date.
	 * 
	 * @param l
	 *            the l
	 * @return the date
	 */
	public static Date toJavaDate(final long l) {
		Date result = new Date();
		result.setTime(l);
		return result;
	}

	/**
	 * To java date.
	 * 
	 * @param ls
	 *            the ls
	 * @return the collection
	 */
	public static Collection<Date> toJavaDate(final long[] ls) {
		Collection<Date> result = new ArrayList<Date>();
		for (long l : ls) {
			result.add(DominoUtils.toJavaDate(l));
		}
		return result;
	}

	/**
	 * To java date safe.
	 * 
	 * @param dt
	 *            the dt
	 * @return the date
	 */
	public static Date toJavaDateSafe(final lotus.domino.DateTime dt) {
		Date date = null;
		if (dt != null) {
			if (dt instanceof org.openntf.domino.DateTime) {
				date = ((org.openntf.domino.DateTime) dt).toJavaDate(); // no need to recycle 'cause it's not toxic
			} else {
				try {
					date = dt.toJavaDate();
				} catch (Throwable t) {
					t.printStackTrace();
				} finally {
					DominoUtils.incinerate(dt);
				}
			}
		}
		return date;
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
		String newVal = Factory.getSession(SessionType.CURRENT).getEnvironmentString(propertyName, true);
		if (!"".equals(newVal)) {
			return newVal;
		} else {
			return defaultValue;
		}
	}

	/**
	 * Gets properties file and returns as an InputStream.
	 * 
	 * @param fileType
	 *            int passed to switch statement. <br/>
	 *            1 -> name of a properties file in this package<br/>
	 *            2 -> literal path of a properties file<br/>
	 *            3 -> relative path of a properties file, relative to Domino <data> directory
	 * @param fileLoc
	 *            String filepath location of properties file
	 * @return InputStream (or BufferedInputStream) of properties file content
	 */
	public static InputStream getDominoProps(final int fileType, final String fileLoc) {
		InputStream returnStream = null;
		InputStream is;
		try {
			switch (fileType) {
			case 1:
				// Properties file in this package
				returnStream = DominoUtils.class.getResourceAsStream(fileLoc);
				break;
			case 2:
				// File in file system at literal path
				is = new FileInputStream(fileLoc);
				returnStream = new BufferedInputStream(is);
				break;
			case 3:
				// File in file system relative to data directory
				String dirPath = getDominoIniVar("Directory", "");
				is = new FileInputStream(dirPath + "/" + fileLoc);
				returnStream = new BufferedInputStream(is);
				break;
			// TODO Need to work out how to get from properties file in NSF
			}
			return returnStream;
		} catch (Throwable e) {
			handleException(e);
			return returnStream;
		}
	}

	public static Item itemFromCalendar(final Item item, final Calendar cal) {
		DateTime dt = item.getAncestorSession().createDateTime(cal);
		item.setDateTimeValue(dt);
		DominoUtils.incinerate(dt);
		return item;
	}

	public static Item itemFromCalendarAppend(final Item item, final Calendar cal) {
		Session sess = item.getAncestorSession();
		DateTime dt = sess.createDateTime(cal);
		Vector<DateTime> v = item.getValueDateTimeArray();
		v.add(dt);
		item.setValues(v);
		sess.getFactory().recycle(dt);
		return item;
	}

	public static Item itemFromDate(final Item item, final Date cal) {
		Session sess = item.getAncestorSession();
		DateTime dt = sess.createDateTime(cal);
		item.setDateTimeValue(dt);
		sess.getFactory().recycle(dt);
		return item;
	}

	public static Item itemFromDateAppend(final Item item, final Date cal) {
		Session sess = item.getAncestorSession();
		DateTime dt = sess.createDateTime(cal);
		Vector<DateTime> v = item.getValueDateTimeArray();
		v.add(dt);
		item.setValues(v);
		sess.getFactory().recycle(dt);
		return item;
	}

	public static Calendar itemToCalendar(final Item item) {
		DateTime dt = item.getDateTimeValue();
		if (dt != null) {
			return DominoUtils.toJavaCalendarSafe(dt);
		} else {
			return null;
		}
	}

	public static Date itemToDate(final Item item) {
		DateTime dt = item.getDateTimeValue();
		if (dt != null) {
			return DominoUtils.toJavaDateSafe(dt);
		} else {
			return null;
		}
	}

	@Deprecated
	// RPr: I'm not sure for what this method is. DateTime is serializable now!?
	public static boolean isSerializable(final Collection<?> values) {
		if (values == null)
			return false;
		boolean result = true;
		Iterator<?> it = values.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof DateTime) {
				return false;
			}
			if (!(o instanceof Serializable)) {
				result = false;
				break;
			}
		}
		return result;
	}

	public static String toNameString(final Name name) {
		String result = "";
		if (!name.isHierarchical()) {
			result = name.getCommon();
		} else {
			result = name.getCanonical();
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static Collection<Comparable> toComparable(final Collection<?> values) {
		Collection<Serializable> colls = toSerializable(values);
		Collection<Comparable> result = new ArrayList<Comparable>();
		if (colls != null && !colls.isEmpty()) {
			for (Serializable ser : colls) {
				if (ser instanceof Comparable) {
					result.add((Comparable) ser);
				} else {
					log_.info("Unable to convert to Comparable from " + ser.getClass().getName());
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Collection<Serializable> toSerializable(final Collection<?> values) {
		if (DominoUtils.isSerializable(values))
			return (Collection<Serializable>) values;
		Collection<Serializable> result = new ArrayList<Serializable>();
		if (values != null && !values.isEmpty()) {
			Iterator<?> it = values.iterator();

			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof DateTime) {
					Date date = null;
					DateTime dt = (DateTime) o;
					date = dt.toJavaDate();
					result.add(date);
				} else if (o instanceof Name) {
					result.add(DominoUtils.toNameString((Name) o));
				} else if (o instanceof String) {
					result.add((String) o);
				} else if (o instanceof Number) {
					result.add((Number) o);
				}
			}
		}
		return result;
	}

}
