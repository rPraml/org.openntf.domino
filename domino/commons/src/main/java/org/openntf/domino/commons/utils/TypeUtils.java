package org.openntf.domino.commons.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import org.openntf.domino.commons.IDataConverter;
import org.openntf.domino.commons.IDateTime;
import org.openntf.domino.commons.IDateTime.ISO;
import org.openntf.domino.commons.IName;
import org.openntf.domino.commons.Strings;
import org.openntf.domino.commons.exception.DataNotCompatibleException;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;

/**
 * TypeUtils does a lot of conversions. central method is {@link #objectToClass(Object, Class)} Convert scalars/scalars
 * <table border=1>
 * <tr>
 * <th>to \ from</th>
 * <th>byte</th>
 * <th>short</th>
 * <th>int</th>
 * <th>long</th>
 * <th>float</th>
 * <th>double</th>
 * <th>CharSequence</th>
 * <th>byte</th>
 * <th>byte</th>
 * <th>byte</th>
 * 
 * </tr>
 * </table>
 * 
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public enum TypeUtils {
	;
	public static final String[] DEFAULT_STR_ARRAY = { "" };

	/**
	 * Converts the given object in a vector.
	 * <ul>
	 * <li>Returns <code>null</code> if the parameter was null</li>
	 * <li>Returns a {@link Vector} with all elements, if the parameter was an {@link Array}, {@link Iterable} or {@link Enumeration}</li>
	 * <li>Returns a {@link Vector} of containing the parameter (Size=1) if the parameter is no "multi-value"
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector toVector(final Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof Vector) {
			return (Vector) o;
		}
		if (o.getClass().isArray()) {
			int l = Array.getLength(o);
			Vector dst = new Vector(l);
			for (int i = 0; i < l; i++) {
				dst.add(Array.get(o, i));
			}
			return dst;
		}
		if (o instanceof List) {
			Vector dst = new Vector(((Collection) o).size());
			for (int i = 0; i < ((List) o).size(); i++) {
				dst.add(((List) o).get(i));
			}
			return dst;
		}
		if (o instanceof Collection) {
			Vector dst = new Vector(((Collection) o).size());
			for (Object collObj : (Collection) o) {
				dst.add(collObj);
			}
			return dst;
		}
		if (o instanceof Iterable) {
			Vector dst = new Vector();
			for (Object collObj : (Iterable) o) {
				dst.add(collObj);
			}
			return dst;
		}
		if (o instanceof Enumeration) {
			Vector dst = new Vector();
			Enumeration e = (Enumeration) o;
			while (e.hasMoreElements()) {
				dst.add(e.nextElement());
			}
			return dst;
		}
		// it is not an array or an iterable
		Vector dst = new Vector(1);
		dst.add(o);
		return dst;
	}

	/**
	 * This is nearly the same as {@link #toVector(Object)}, but a bit more efficient than a Vector, because it uses an {@link ArrayList}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List toList(final Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof List) {
			return (List) o;
		}
		if (o.getClass().isArray()) {
			int l = Array.getLength(o);
			List dst = new ArrayList(l);
			for (int i = 0; i < l; i++) {
				dst.add(Array.get(o, i));
			}
			return dst;
		}

		if (o instanceof Collection) {
			List dst = new ArrayList(((Collection) o).size());
			for (Object collObj : (Collection) o) {
				dst.add(collObj);
			}
			return dst;
		}
		if (o instanceof Iterable) {
			List dst = new ArrayList();
			for (Object collObj : (Iterable) o) {
				dst.add(collObj);
			}
			return dst;
		}
		if (o instanceof Enumeration) {
			List dst = new ArrayList();
			Enumeration e = (Enumeration) o;
			while (e.hasMoreElements()) {
				dst.add(e.nextElement());
			}
			return dst;
		}
		// it is not an array or an iterable
		List dst = new ArrayList(1);
		dst.add(o);
		return dst;
	}

	/**
	 * Creates an empty collection of the given type, preinitialized with the length
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static <T extends Collection> T createCollection(Class<T> collType, final int length) {
		if (!Collection.class.isAssignableFrom(collType)) {
			throw new IllegalArgumentException(collType.getName() + " is not a collection");
		}
		try {
			// handle Interfaces
			if (List.class == collType || Collection.class == collType) {
				collType = (Class<T>) ArrayList.class;
			}
			if (Set.class == collType) {
				collType = (Class<T>) HashSet.class;
			}
			if (length == -1) {
				return collType.newInstance();
			}

			Constructor<T> cTor = collType.getConstructor(int.class);
			return cTor.newInstance(length);
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot create a collection of type " + collType, e);
		}
	}

	@SuppressWarnings("rawtypes")
	protected static Object getFirst(final Object o) {
		if (o == null)
			return null;
		if (o.getClass().isArray()) {
			if (Array.getLength(o) == 0)
				return null;
			return Array.get(o, 0);
		}
		if (o instanceof List) {
			if (((List) o).isEmpty())
				return null;
			return ((List) o).get(0);
		}
		if (o instanceof Iterable) {
			Iterator it = ((Iterable) o).iterator();
			if (!it.hasNext())
				return null;
			return it.next();
		}
		return o;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends Collection> T toCollection(final Object o, final Class<T> collType) {
		if (o == null) {
			return null;
		}
		if (collType.isAssignableFrom(o.getClass())) {
			return (T) o;
		}

		if (o.getClass().isArray()) {
			int l = Array.getLength(o);
			T dst = createCollection(collType, l);
			for (int i = 0; i < l; i++) {
				dst.add(Array.get(o, i));
			}
			return dst;
		}
		if (o instanceof List) {
			T dst = createCollection(collType, ((Collection) o).size());
			for (int i = 0; i < ((List) o).size(); i++) {
				dst.add(((List) o).get(i));
			}
			return dst;
		}
		if (o instanceof Collection) {
			T dst = createCollection(collType, ((Collection) o).size());
			for (Object collObj : (Collection) o) {
				dst.add(collObj);
			}
			return dst;
		}
		if (o instanceof Iterable) {
			T dst = createCollection(collType, -1);
			for (Object collObj : (Iterable) o) {
				dst.add(collObj);
			}
			return dst;
		}
		if (o instanceof Enumeration) {
			T dst = createCollection(collType, -1);
			Enumeration e = (Enumeration) o;
			while (e.hasMoreElements()) {
				dst.add(e.nextElement());
			}
			return dst;
		}
		// it is not an array or an iterable
		T dst = createCollection(collType, 1);
		dst.add(o);
		return dst;
	}

	public static <T> T objectToClass(final Object o, final Class<T> targetType, final T fallback) {
		try {
			return objectToClass(o, targetType);
		} catch (DataNotCompatibleException dne) {
			return fallback;
		}
	}

	/**
	 * Will do a type conversion
	 * 
	 * 
	 * Expected targetType:
	 * <ul>
	 * 
	 * <li><b>a primitive:</b></li> will throw a {@link DataNotCompatibleException}. because it is not possible to return primitives.
	 * 
	 * <li><b>assignable from source:</b></li> source will be returned, because source is an instance of targetType and no conversion is
	 * neccessary.
	 * 
	 * <li><b>CharSequence[] or String[]:</b></li> Will perform a {@link Strings#toStrings(Object)}. In the case that source is a Iterable
	 * or Array, it will toString() every element and returns an array with the same size. If source is not a multi-value type, it will
	 * return a String array of size 1, that contains the toString representation of source.
	 * 
	 * <li><b>CharSequence or String:</b></li> Will perform a {@link Strings#toString(Object)}. Multi-Value types are concanated and
	 * separated with ", ". All other types are just toString()'d.
	 * 
	 * <li><b>Enum:</b></li> Source is converted to string and then a lookup is done for every enum constant.
	 * 
	 * <li><b>Enum[]:</b></li> Same as enum, but returns an array. If Source is a multi-value, the array has the same size, othewise the
	 * array has the size 1
	 * 
	 * <li><b>byte[], short[], int[], long[], float[], double[]:</b></li> Will try to extract the numeric value of <code>source</code> and
	 * performs a typesafe cast to the given datatype. If source is/contains a CharSequence, it will <code>toString()</code> it and tries to
	 * parse the number. NOT locale dependent!
	 * 
	 * <li><b>char[]:</b></li> For CharSequences that are in source, the first character is returned (or 0 if the CharSequence is empty).
	 * For nummeric values, a typesafe cast is done. <font color=red>For multi values, it will return the first letter of each multiValue.
	 * It will never return {@link String#toCharArray()}.</font> If you need this, request a String or String[].
	 * 
	 * <li><b>boolean[]:</b></li> Will convert numeric values != 0 to true. Will convert Strings starting with a digit 0..9 to true. Will
	 * convert the String containing "true" (case insensitive) to <code>true</code>. Everything else is converted to <code>false</code>
	 * 
	 * <li><b>List, Vector</b></li> Returns the object as {@link Vector} or {@link ArrayList}
	 * 
	 * <li><b>Iterable</b></li> If the <code>Collection</code> or <code>Iterable</code> interface is passed, a ArrayList is created. For
	 * <code>Set</code> a HashSet is used. For all other classes, either the constructors &lt;init&gt;() or &lt;init&gt;(int) are called.
	 * This depends if the initial size can be predicted.
	 * 
	 * </ul>
	 * <p>
	 * For the following types there are special cases:
	 * </p>
	 * 
	 * <ul>
	 * <li><b>Pattern</b></li> toString()s source and compiles it as {@link Pattern}
	 * 
	 * <li><b>IName</b></li> toString()s source and parses it as {@link IName}
	 * 
	 * <li><b>Class</b></li> toString()s source and tries to create a new Class
	 * 
	 * <li><b>Date, Calendar</b></li> tries to treat nummeric values as timestamps and Strings as Date-String
	 * 
	 * </ul>
	 * 
	 * For all other types the following construction is tried:
	 * 
	 * @param source
	 * @param targetType
	 *            the desired targetType
	 * 
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T objectToClass(Object source, final Class<T> targetType) {
		if (source == null) {
			return null;
		}
		if (targetType.isPrimitive()) {
			// This will NOT work
			throw new DataNotCompatibleException("Cannot convert to primitive datatyte. "
					+ "Either you use an array or you use the appropriate wrapper for the primitive.");
		}
		Class<?> sourceType = source.getClass();
		if (targetType == sourceType || targetType.isAssignableFrom(sourceType)) {
			return (T) source;
		}
		// handle strings with priority
		if (String[].class.equals(targetType) || CharSequence[].class.equals(targetType)) {
			return (T) Strings.toStrings(source);
		}

		if (String.class.equals(targetType) || CharSequence.class.equals(targetType)) {
			return (T) Strings.toString(source);
		}

		if (targetType.isEnum()) {
			return (T) toEnum(source, (Class<? extends Enum>) targetType);
		}

		if (targetType.isArray()) {
			Class<?> cType = targetType.getComponentType();
			if (cType.isPrimitive()) {
				if (cType == Byte.TYPE) {
					return (T) toByteArray(source);
				}
				if (cType == Short.TYPE) {
					return (T) toShortArray(source);
				}
				if (cType == Integer.TYPE) {
					return (T) toIntArray(source);
				}
				if (cType == Long.TYPE) {
					return (T) toLongArray(source);
				}
				if (cType == Float.TYPE) {
					return (T) toFloatArray(source);
				}
				if (cType == Double.TYPE) {
					return (T) toDoubleArray(source);
				}
				if (cType == Character.TYPE) {
					return (T) toCharArray(source);
				}
				if (cType == Boolean.TYPE) {
					return (T) toBooleanArray(source);
				}
				// This should never happen, unless new primitives are introduced 
				throw new DataNotCompatibleException("Primitive " + cType.getName() + " is not supported!");
			}

			if (sourceType.isArray()) {
				Object[] dst = (Object[]) Array.newInstance(cType, Array.getLength(source));
				for (int i = 0; i < dst.length; i++) {
					dst[i] = objectToClass(Array.get(source, i), cType);
				}
				return (T) dst;
			}
			if (source instanceof List) {
				Object[] dst = (Object[]) Array.newInstance(cType, ((Collection) source).size());
				for (int i = 0; i < ((List) source).size(); i++) {
					dst[i] = objectToClass(((List) source).get(i), cType);
				}
				return (T) dst;
			}
			if (source instanceof Collection) {
				Object[] dst = (Object[]) Array.newInstance(cType, ((Collection) source).size());
				int i = 0;
				for (Object collObj : (Collection) source) {
					dst[i++] = objectToClass(collObj, cType);
				}
				return (T) dst;
			}
			if (source instanceof Iterable) {
				List li = new ArrayList();
				for (Object collObj : (Iterable) source) {
					li.add(collObj);
				}
				return objectToClass(li, targetType);
			}

			if (source instanceof Enumeration) {
				List li = new ArrayList();
				Enumeration e = (Enumeration) source;
				while (e.hasMoreElements()) {
					li.add(e.nextElement());
				}
				return objectToClass(li, targetType);
			}
			// it is not an array or an iterable
			Object[] ret = (Object[]) Array.newInstance(cType, 1);
			ret[0] = objectToClass(source, cType);
			return (T) ret;
		}
		if (targetType == Vector.class) {
			return (T) toVector(source);
		}
		if (targetType == List.class) {
			return (T) toList(source);
		}

		if (Iterable.class.isAssignableFrom(targetType)) {
			return (T) toCollection(source, (Class<? extends Collection>) targetType);
		}

		// convert requested primitive types
		source = getFirst(source);
		if (source == null) {
			return null;
		}
		sourceType = source.getClass();
		if (targetType.isAssignableFrom(sourceType)) {
			return (T) source;
		}

		if (targetType == Byte.class) {
			return (T) Byte.valueOf(toByte(source));
		}
		if (targetType == Short.class) {
			return (T) Short.valueOf(toShort(source));
		}
		if (targetType == Integer.class) {
			return (T) Integer.valueOf(toInt(source));
		}
		if (targetType == Long.class) {
			return (T) Long.valueOf(toLong(source));
		}
		if (targetType == Float.class) {
			return (T) Float.valueOf(toFloat(source));
		}
		if (targetType == Double.class) {
			return (T) Double.valueOf(toDouble(source));
		}
		if (targetType == Character.class) {
			return (T) Character.valueOf(toChar(source));
		}
		if (targetType == Boolean.class) {
			return (T) Boolean.valueOf(toBoolean(source));
		}

		// now we have to convert some basic types

		// Pattern
		if (targetType.isAssignableFrom(Pattern.class)) {
			return (T) Pattern.compile(Strings.toString(source));
		}
		// Name
		if (targetType.isAssignableFrom(IName.class)) {
			return (T) IName.$.create(Strings.toString(source));
		}

		// Class
		if (targetType.isAssignableFrom(Class.class)) {
			String clName = Strings.toString(source);
			try {
				return (T) ThreadUtils.getClass(clName);
			} catch (ClassNotFoundException e) {
				throw new DataNotCompatibleException("Cannot load class: " + clName, e);
			}

		}

		// IFormula - which locale should be used here?
		//		if (targetType.isAssignableFrom(IFormula.class)) {
		//			IFormulaService service = IFormulaService.$.getInstance()
		//			String formula = Strings.toString(source);
		//			try {
		//				return (T) service.parse(formula);
		//			} catch (FormulaParseException e) {
		//				throw new DataNotCompatibleException("Cannot parse formula: " + formula, e);
		//			}
		//		}

		// Date
		if (targetType.isAssignableFrom(Date.class)) {
			return (T) toDate(source);
		}
		// icu-calendar
		if (targetType.isAssignableFrom(Calendar.class)) {
			return (T) toCalendar(source);
		}
		// Try to invoke default constructor
		try {
			Constructor<T> cTor = getMatchingConstructor(targetType, sourceType);
			if (cTor != null) {
				return cTor.newInstance(source);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// and then fallback to IDataConverter
		// TODO RPr: this is not yet finished! => JFOF-15
		for (IDataConverter dc : IDataConverter.$.getInstances()) {
			if (targetType.isAssignableFrom(dc.getType())) {
				return (T) dc.convertTo(source);
			}
		}

		throw new DataNotCompatibleException("Cannot convert a " + sourceType.getName() + " to " + targetType.getName());
	}

	public static Date toDate(Object source) {
		source = getFirst(source);
		if (source == null)
			return null;
		if (source instanceof Date)
			return (Date) source;
		try {
			if (source instanceof IDateTime) {
				return ((IDateTime) source).toJavaDate();
			}

			if (source instanceof com.ibm.icu.util.Calendar) {
				return ((com.ibm.icu.util.Calendar) source).getTime();
			}

			if (source instanceof java.util.Calendar) {
				return ((java.util.Calendar) source).getTime();
			}

			if (source instanceof CharSequence) {
				return parseIsoDate(Strings.toString(source)).getTime();
			}

			if (source instanceof Number) {
				return new Date(toLong(source));
			}
			// at last: ask the service
			// and then fallback to IDataConverter
			// TODO RPr: this is not yet finished! => JFOF-15
			for (IDataConverter<?> dc : IDataConverter.$.getInstances()) {
				if (Date.class.isAssignableFrom(dc.getType())) {
					return (Date) dc.convertTo(source);
				}
			}
			throw new DataNotCompatibleException("Cannot convert a " + source.getClass().getName() + " to java.util.Date");
		} catch (Exception e) {
			throw new DataNotCompatibleException("Cannot convert " + source + " to java.util.Date", e);
		}
	}

	public static Calendar toCalendar(Object source) {
		source = getFirst(source);
		if (source == null)
			return null;
		if (source instanceof Calendar)
			return (Calendar) source;
		try {
			if (source instanceof IDateTime) {
				return ((IDateTime) source).toJavaCal();
			}

			if (source instanceof java.util.Date) {
				Calendar cal = Calendar.getInstance();
				cal.setTime((java.util.Date) source);
				return cal;
			}

			if (source instanceof CharSequence) {
				return parseIsoDate(Strings.toString(source));
			}

			if (source instanceof Number) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(toLong(source));
				return cal;
			}
			// at last: ask the service
			// TODO RPr: this is not yet finished! => JFOF-15
			for (IDataConverter<?> dc : IDataConverter.$.getInstances()) {
				if (Calendar.class.isAssignableFrom(dc.getType())) {
					return (Calendar) dc.convertTo(source);
				}
			}
			throw new DataNotCompatibleException("Cannot convert a " + source.getClass().getName() + " to java.util.Date");
		} catch (Exception e) {
			throw new DataNotCompatibleException("Cannot convert " + source + " to java.util.Date", e);
		}
	}

	/**
	 * Finds the best matching constructor for a given class & parameter
	 * 
	 * @param clazz
	 * @param givenArg
	 * @return
	 */
	private static <T> Constructor<T> getMatchingConstructor(final Class<T> clazz, final Class<?> givenArg) {
		try {
			// try direct
			return clazz.getConstructor(givenArg);
		} catch (NoSuchMethodException e) {
		}
		// handle common cases
		if (Integer.class.isAssignableFrom(givenArg)) {
			try {
				return clazz.getConstructor(int.class);
			} catch (NoSuchMethodException e) {
			}
			try {
				return clazz.getConstructor(long.class);
			} catch (NoSuchMethodException e) {
			}
		}
		if (Long.class.isAssignableFrom(givenArg)) {
			try {
				return clazz.getConstructor(long.class);
			} catch (NoSuchMethodException e) {
			}
			try {
				return clazz.getConstructor(int.class);
			} catch (NoSuchMethodException e) {
			}
		}
		if (Double.class.isAssignableFrom(givenArg)) {
			try {
				return clazz.getConstructor(double.class);
			} catch (NoSuchMethodException e) {
			}
			try {
				return clazz.getConstructor(long.class);
			} catch (NoSuchMethodException e) {
			}
			try {
				return clazz.getConstructor(int.class);
			} catch (NoSuchMethodException e) {
			}
		}

		// search through all methods TODO: this may not always find the correct constructor

		Constructor<T>[] ctors = (Constructor<T>[]) clazz.getConstructors();
		Constructor<T> bestMatch = null;
		int matchLevel = 0;
		for (int i = 0, size = ctors.length; i < size; i++) {
			// compare parameters
			Class<?>[] ctorParams = ctors[i].getParameterTypes();
			if (ctorParams.length == 1) {
				Class<?> ctorArg = ctorParams[0];
				if (ctorArg.isAssignableFrom(givenArg)) {
					return ctors[i];
				}
				if (ctorArg.isPrimitive()) {
					if (ctorArg == Byte.TYPE) {
						if (givenArg == Byte.class)
							return ctors[i];
						if (matchLevel < 1) {
							matchLevel = 1;
							bestMatch = ctors[i];
						}
					}
					if (ctorArg == Short.TYPE) {
						if (givenArg == Short.class)
							return ctors[i];
						if (matchLevel < 2) {
							matchLevel = 2;
							bestMatch = ctors[i];
						}
					}
					if (ctorArg == Integer.TYPE) {
						if (givenArg == Integer.class)
							return ctors[i];
						if (matchLevel < 3) {
							matchLevel = 3;
							bestMatch = ctors[i];
						}
					}
					if (ctorArg == Long.TYPE) {
						if (givenArg == Long.class)
							return ctors[i];
						if (matchLevel < 4) {
							matchLevel = 4;
							bestMatch = ctors[i];
						}
					}

					if (ctorArg == Float.TYPE) {
						if (givenArg == Float.class)
							return ctors[i];
						if (matchLevel < 5) {
							matchLevel = 5;
							bestMatch = ctors[i];
						}
					}

					if (ctorArg == Double.TYPE) {
						if (givenArg == Double.class)
							return ctors[i];
						if (matchLevel < 6) {
							matchLevel = 6;
							bestMatch = ctors[i];
						}
					}

					if (ctorArg == Character.TYPE && givenArg == Character.class)
						return ctors[i];
					if (ctorArg == Boolean.TYPE && givenArg == Boolean.class)
						return ctors[i];
				}
			}
		}
		if (bestMatch != null && Number.class.isAssignableFrom(givenArg))
			return bestMatch;

		return null;
	}

	/**
	 * Converts the given value to an enum
	 * 
	 * @param value
	 *            the enum-name (contained as String) - for compatible reasons this can either contain the enum-name or the enum-classname
	 *            and the enum-name, separated by a space. But the class name will never be used
	 * @param enumClass
	 *            the enum-class
	 * @return the enum
	 * @throws DataNotCompatibleException
	 *             if there were conversion errors
	 */
	@SuppressWarnings("rawtypes")
	public static <T extends Enum> T toEnum(final Object value, final Class<T> enumClass) throws DataNotCompatibleException {
		if (value == null)
			return null;

		T result = null;
		String ename = objectToClass(value, String.class);
		if (ename.contains(" ")) {
			ename = ename.substring(ename.indexOf(' ') + 1).trim();
			// RPr: We should not try to load enum classes. You can probably get nice Identity problems if enums are 
			// loaded by different classLoaders. If you want an enum, specify the enum
		}

		try {
			return (T) Enum.valueOf(enumClass, ename);
		} catch (IllegalArgumentException iae) {

		}

		T[] objs = enumClass.getEnumConstants();
		if (objs.length > 0) {
			for (T obj : objs) {
				if (obj.name().equals(ename)) {
					result = obj;
					break;
				}
			}
		}
		if (result == null) {
			throw new DataNotCompatibleException("Unable to discover an Enum by the name of " + ename + " in class " + enumClass
					+ ". Available Enums: " + Strings.toString(objs));
		}
		return result;
	}

	//--------- byte
	/**
	 * Converts the value to byte.
	 * 
	 * @param value
	 *            may be a scalar, a collection or a array.
	 */
	public static byte toByte(Object value) {
		value = getFirst(value);
		if (value == null) {
			return 0;
		}
		if (value instanceof Byte) {
			return (Byte) value;
		}
		if (value instanceof CharSequence) {
			CharSequence cs = (CharSequence) value;
			if (cs.length() == 0)
				return 0;
			try {
				return Byte.valueOf(cs.toString());
			} catch (NumberFormatException nfe) {
				throw new DataNotCompatibleException("Cannot convert '" + cs.toString() + "' to number", nfe);
			}

		}
		if (value instanceof Number) {
			if (value instanceof Float || value instanceof Double) {
				return SafeCast.doubleToByte(((Number) value).doubleValue());
			} else {
				return SafeCast.longToByte(((Number) value).longValue());
			}
		}

		if (value instanceof Character) {
			return SafeCast.longToByte(((Character) value).charValue());
		}
		//		for (IDataConverter dc : IDataConverter.$.getInstance())) {
		//			T ret = dc.toByte(o);
		//			if (ret != null)
		//				return ret;
		//		}
		throw new DataNotCompatibleException("Cannot convert a " + value.getClass().getName() + " to byte primitive.");
	}

	/**
	 * Converts a Collection<?> to byte[]
	 */
	public static <T> byte[] toByteArray(final Collection<T> coll) {
		if (coll == null)
			return null;
		byte[] ret = new byte[coll.size()];
		Iterator<T> iterator = coll.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toByte(iterator.next());
		}
		return ret;
	}

	/**
	 * Converts a List<?> to byte[]
	 */
	public static <T> byte[] toByteArray(final List<T> list) {
		if (list == null)
			return null;
		byte[] ret = new byte[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toByte(list.get(i));
		}
		return ret;
	}

	/**
	 * Converts a Object to byte[]
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static byte[] toByteArray(final Object arr) {
		if (arr == null)
			return null;
		Class<?> type = arr.getClass();
		if (type.isArray()) {
			Class<?> cType = type.getComponentType();
			if (cType == Byte.TYPE)
				return (byte[]) arr;

			byte[] ret = new byte[Array.getLength(arr)];
			if (cType == Short.TYPE || cType == Integer.TYPE || cType == Long.TYPE || cType == Character.TYPE) {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = SafeCast.longToByte(Array.getLong(arr, i));
				}
			} else if (cType == Float.TYPE || cType == Double.TYPE) {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = SafeCast.doubleToByte(Array.getDouble(arr, i));
				}
			} else {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = toByte(Array.get(arr, i));
				}
			}

			return ret;
		}
		if (arr instanceof List) {
			return toByteArray((List) arr);
		}
		if (arr instanceof Collection) {
			return toByteArray((Collection) arr);
		}
		return new byte[] { toByte(arr) };
	}

	//--------- short

	/**
	 * Converts the value to short.
	 * 
	 * @param value
	 *            may be a scalar, a collection or a array.
	 */
	public static short toShort(Object value) {
		value = getFirst(value);
		if (value == null) {
			return 0;
		}
		if (value instanceof Short) {
			return (Short) value;
		}
		if (value instanceof CharSequence) {
			CharSequence cs = (CharSequence) value;
			if (cs.length() == 0)
				return 0;
			try {
				return Short.valueOf(cs.toString());
			} catch (NumberFormatException nfe) {
				throw new DataNotCompatibleException("Cannot convert '" + cs.toString() + "' to number", nfe);
			}

		}

		if (value instanceof Number) {
			if (value instanceof Float || value instanceof Double) {
				return SafeCast.doubleToShort(((Number) value).doubleValue());
			} else {
				return SafeCast.longToShort(((Number) value).longValue());
			}
		}
		if (value instanceof Character) {
			return SafeCast.longToShort(((Character) value).charValue());
		}
		throw new DataNotCompatibleException("Cannot convert a " + value.getClass().getName() + " to short primitive.");
	}

	/**
	 * Converts a Collection<?> to short[]
	 */
	public static <T> short[] toShortArray(final Collection<T> coll) {
		if (coll == null)
			return null;
		short[] ret = new short[coll.size()];
		Iterator<T> iterator = coll.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toShort(iterator.next());
		}
		return ret;
	}

	/**
	 * Converts a List<?> to short[]
	 */
	public static <T> short[] toShortArray(final List<T> list) {
		if (list == null)
			return null;
		short[] ret = new short[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toShort(list.get(i));
		}
		return ret;
	}

	/**
	 * Converts a Object to short[]
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static short[] toShortArray(final Object arr) {
		if (arr == null)
			return null;
		Class<?> type = arr.getClass();
		if (type.isArray()) {
			Class<?> cType = type.getComponentType();
			if (cType == Short.TYPE)
				return (short[]) arr;

			short[] ret = new short[Array.getLength(arr)];
			if (cType == Byte.TYPE || cType == Integer.TYPE || cType == Long.TYPE) {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = SafeCast.longToShort(Array.getLong(arr, i));
				}
			} else if (cType == Float.TYPE || cType == Double.TYPE) {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = SafeCast.doubleToShort(Array.getDouble(arr, i));
				}
			} else {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = toShort(Array.get(arr, i));
				}
			}

			return ret;
		}

		if (arr instanceof List) {
			return toShortArray((List) arr);
		}
		if (arr instanceof Collection) {
			return toShortArray((Collection) arr);
		}
		return new short[] { toShort(arr) };
	}

	//--------- int

	/**
	 * Converts the value to int.
	 * 
	 * @param value
	 *            may be a scalar, a collection or a array.
	 */
	public static int toInt(Object value) {
		value = getFirst(value);
		if (value == null) {
			return 0;
		}
		if (value instanceof Integer) {
			return (Integer) value;
		}
		if (value instanceof CharSequence) {
			CharSequence cs = (CharSequence) value;
			if (cs.length() == 0)
				return 0;
			try {
				return Integer.valueOf(cs.toString());
			} catch (NumberFormatException nfe) {
				throw new DataNotCompatibleException("Cannot convert '" + cs.toString() + "' to number", nfe);
			}
		}
		if (value instanceof Number) {
			if (value instanceof Float || value instanceof Double) {
				return SafeCast.doubleToInt(((Number) value).doubleValue());
			} else {
				return SafeCast.longToInt(((Number) value).longValue());
			}
		}
		if (value instanceof Character) {
			return SafeCast.longToInt(((Character) value).charValue());
		}
		throw new DataNotCompatibleException("Cannot convert a " + value.getClass().getName() + " to int primitive.");
	}

	/**
	 * Converts a Collection<?> to int[]
	 */
	public static <T> int[] toIntArray(final Collection<T> coll) {
		if (coll == null)
			return null;
		int[] ret = new int[coll.size()];
		Iterator<T> iterator = coll.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toInt(iterator.next());
		}
		return ret;
	}

	/**
	 * Converts a List<?> to int[]
	 */
	public static <T> int[] toIntArray(final List<T> list) {
		if (list == null)
			return null;
		int[] ret = new int[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toInt(list.get(i));
		}
		return ret;
	}

	/**
	 * Converts a Object to int[]
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int[] toIntArray(final Object arr) {
		if (arr == null)
			return null;
		Class<?> type = arr.getClass();
		if (type.isArray()) {
			Class<?> cType = type.getComponentType();
			if (cType == Integer.TYPE)
				return (int[]) arr;

			int[] ret = new int[Array.getLength(arr)];
			if (cType == Byte.TYPE || cType == Short.TYPE || cType == Long.TYPE) {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = SafeCast.longToInt(Array.getLong(arr, i));
				}
			} else if (cType == Float.TYPE || cType == Double.TYPE) {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = SafeCast.doubleToInt(Array.getDouble(arr, i));
				}
			} else {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = toInt(Array.get(arr, i));
				}
			}

			return ret;
		}
		if (arr instanceof List) {
			return toIntArray((List) arr);
		}
		if (arr instanceof Collection) {
			return toIntArray((Collection) arr);
		}
		return new int[] { toInt(arr) };
	}

	//--------- long

	/**
	 * Converts the value to long.
	 * 
	 * @param value
	 *            may be a scalar, a collection or a array.
	 */
	public static long toLong(Object value) {
		value = getFirst(value);
		if (value == null) {
			return 0;
		}
		if (value instanceof Long) {
			return (Long) value;
		}
		if (value instanceof CharSequence) {
			CharSequence cs = (CharSequence) value;
			if (cs.length() == 0)
				return 0;
			try {
				return Long.valueOf(cs.toString());
			} catch (NumberFormatException nfe) {
				throw new DataNotCompatibleException("Cannot convert '" + cs.toString() + "' to number", nfe);
			}
		}
		if (value instanceof Number) {
			if (value instanceof Float || value instanceof Double) {
				return SafeCast.doubleToLong(((Number) value).doubleValue());
			} else {
				return ((Number) value).longValue();
			}
		}
		if (value instanceof Date) {
			return ((Date) value).getTime();
		}
		if (value instanceof Calendar) {
			return ((Calendar) value).getTimeInMillis();
		}

		if (value instanceof IDateTime) {
			return ((IDateTime) value).toJavaDate().getTime();
		}
		if (value instanceof Character) {
			return ((Character) value).charValue();
		}
		throw new DataNotCompatibleException("Cannot convert a " + value.getClass().getName() + " to long primitive.");
	}

	/**
	 * Converts a Collection<?> to long[]
	 */
	public static <T> long[] toLongArray(final Collection<T> coll) {
		if (coll == null)
			return null;
		long[] ret = new long[coll.size()];
		Iterator<T> iterator = coll.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toLong(iterator.next());
		}
		return ret;
	}

	/**
	 * Converts a List<?> to long[]
	 */
	public static <T> long[] toLongArray(final List<T> list) {
		if (list == null)
			return null;
		long[] ret = new long[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toLong(list.get(i));
		}
		return ret;
	}

	/**
	 * Converts a Object to long[]
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static long[] toLongArray(final Object arr) {
		if (arr == null)
			return null;
		Class<?> type = arr.getClass();
		if (type.isArray()) {
			Class<?> cType = type.getComponentType();
			if (cType == Long.TYPE)
				return (long[]) arr;
			long[] ret = new long[Array.getLength(arr)];
			if (cType == Byte.TYPE || cType == Short.TYPE || cType == Integer.TYPE) {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = Array.getLong(arr, i);
				}
			} else if (cType == Float.TYPE || cType == Double.TYPE) {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = SafeCast.doubleToLong(Array.getDouble(arr, i));
				}
			} else {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = toLong(Array.get(arr, i));
				}
			}
			return ret;
		}

		if (arr instanceof List) {
			return toLongArray((List) arr);
		}
		if (arr instanceof Collection) {
			return toLongArray((Collection) arr);
		}
		return new long[] { toLong(arr) };
	}

	//--------- float

	/**
	 * Converts the value to float.
	 * 
	 * @param value
	 *            may be a scalar, a collection or a array.
	 */
	public static float toFloat(Object value) {
		value = getFirst(value);
		if (value == null) {
			return 0;
		}
		if (value instanceof Float) {
			return (Float) value;
		}
		if (value instanceof CharSequence) {
			CharSequence cs = (CharSequence) value;
			if (cs.length() == 0)
				return 0;
			try {
				return Float.valueOf(cs.toString());
			} catch (NumberFormatException nfe) {
				throw new DataNotCompatibleException("Cannot convert '" + cs.toString() + "' to number", nfe);
			}

		}
		if (value instanceof Number) {
			if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
				return SafeCast.longToFloat(((Number) value).longValue());
			} else {
				return SafeCast.doubleToFloat(((Number) value).doubleValue());
			}
		}
		if (value instanceof Character) {
			return SafeCast.longToFloat(((Character) value).charValue());
		}
		throw new DataNotCompatibleException("Cannot convert a " + value.getClass().getName() + " to float primitive.");
	}

	/**
	 * Converts a Collection<?> to float[]
	 */
	public static <T> float[] toFloatArray(final Collection<T> coll) {
		if (coll == null)
			return null;
		float[] ret = new float[coll.size()];
		Iterator<T> iterator = coll.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toFloat(iterator.next());
		}
		return ret;
	}

	/**
	 * Converts a List<?> to float[]
	 */
	public static <T> float[] toFloatArray(final List<T> list) {
		if (list == null)
			return null;
		float[] ret = new float[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toFloat(list.get(i));
		}
		return ret;
	}

	/**
	 * Converts a Object to float[]
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static float[] toFloatArray(final Object arr) {
		if (arr == null)
			return null;
		Class<?> type = arr.getClass();
		if (type.isArray()) {
			Class<?> cType = type.getComponentType();
			if (cType == Float.TYPE) // correct type
				return (float[]) arr;
			float[] ret = new float[Array.getLength(arr)];
			if (cType == Byte.TYPE || cType == Short.TYPE || cType == Integer.TYPE || cType == Long.TYPE) {
				// type can be widened
				for (int i = 0; i < ret.length; i++) {
					ret[i] = SafeCast.longToFloat(Array.getLong(arr, i));
				}
			} else if (cType == Double.TYPE) {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = SafeCast.doubleToFloat(Array.getDouble(arr, i));
				}
			} else {
				// type must be converted
				for (int i = 0; i < ret.length; i++) {
					ret[i] = toFloat(Array.get(arr, i));
				}
			}
			return ret;
		}
		if (arr instanceof List) {
			return toFloatArray((List) arr);
		}
		if (arr instanceof Collection) {
			return toFloatArray((Collection) arr);
		}
		return new float[] { toFloat(arr) };
	}

	//--------- double

	/**
	 * Converts the value to double.
	 * 
	 * @param value
	 *            may be a scalar, a collection or a array.
	 */
	public static double toDouble(Object value) {
		value = getFirst(value);
		if (value == null) {
			return 0;
		}
		if (value instanceof Double) {
			return (Double) value;
		}
		if (value instanceof CharSequence) {
			CharSequence cs = (CharSequence) value;
			if (cs.length() == 0)
				return 0;
			try {
				return Double.valueOf(cs.toString());
			} catch (NumberFormatException nfe) {
				throw new DataNotCompatibleException("Cannot convert '" + cs.toString() + "' to number", nfe);
			}

		}
		if (value instanceof Number) {
			if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
				return SafeCast.longToDouble(((Number) value).longValue());
			} else {
				return ((Number) value).doubleValue();
			}
		}

		if (value instanceof Character) {
			return SafeCast.longToDouble(((Character) value).charValue());
		}

		throw new DataNotCompatibleException("Cannot convert a " + value.getClass().getName() + " to double primitive.");
	}

	/**
	 * Converts a Collection<?> to double[]
	 */
	public static <T> double[] toDoubleArray(final Collection<T> coll) {
		if (coll == null)
			return null;
		double[] ret = new double[coll.size()];
		Iterator<T> iterator = coll.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toDouble(iterator.next());
		}
		return ret;
	}

	/**
	 * Converts a List<?> to double[]
	 */
	public static <T> double[] toDoubleArray(final List<T> list) {
		if (list == null)
			return null;
		double[] ret = new double[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toDouble(list.get(i));
		}
		return ret;
	}

	/**
	 * Converts a Object to double[]
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static double[] toDoubleArray(final Object arr) {
		if (arr == null)
			return null;
		Class<?> type = arr.getClass();
		if (type.isArray()) {
			Class<?> cType = type.getComponentType();
			if (cType == Double.TYPE) // correct type
				return (double[]) arr;
			double[] ret = new double[Array.getLength(arr)];
			if (cType == Byte.TYPE || cType == Short.TYPE || cType == Integer.TYPE || cType == Long.TYPE) {
				// type can be widened
				for (int i = 0; i < ret.length; i++) {
					ret[i] = SafeCast.longToDouble(Array.getLong(arr, i));
				}
			} else if (cType == Float.TYPE) {
				for (int i = 0; i < ret.length; i++) {
					ret[i] = Array.getDouble(arr, i);
				}
			} else {
				// type must be converted
				for (int i = 0; i < ret.length; i++) {
					ret[i] = toDouble(Array.get(arr, i));
				}
			}
			return ret;
		}
		if (arr instanceof List) {
			return toDoubleArray((List) arr);
		}
		if (arr instanceof Collection) {
			return toDoubleArray((Collection) arr);
		}
		return new double[] { toDouble(arr) };
	}

	//--------- char
	/**
	 * Converts the value to boolean.
	 * 
	 * @param value
	 *            may be a scalar, a collection or a array. Numeric values and Strings containing 1 or "true" are converted to true.
	 *            Everything else is converted to false
	 */
	public static char toChar(Object value) {
		value = getFirst(value);
		if (value == null) {
			return 0;
		}
		if (value instanceof Character) {
			return (Character) value;
		}
		if (value instanceof CharSequence) {
			CharSequence cs = (CharSequence) value;
			if (cs.length() == 0) {
				return 0;
			}
			return cs.charAt(0);
		}
		if (value instanceof Number) {
			if (value instanceof Float || value instanceof Double) {
				return SafeCast.doubleToChar(((Number) value).doubleValue());
			} else {
				return SafeCast.longToChar(((Number) value).longValue());
			}
		}
		throw new DataNotCompatibleException("Cannot convert a " + value.getClass().getName() + " to char primitive.");
	}

	/**
	 * Converts a Collection<?> to short[]
	 */
	public static <T> char[] toCharArray(final Collection<T> coll) {
		if (coll == null)
			return null;
		char[] ret = new char[coll.size()];
		Iterator<T> iterator = coll.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toChar(iterator.next());
		}
		return ret;
	}

	/**
	 * Converts a List<?> to short[]
	 */
	public static <T> char[] toCharArray(final List<T> list) {
		if (list == null)
			return null;
		char[] ret = new char[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toChar(list.get(i));
		}
		return ret;
	}

	/**
	 * Converts a Object to short[]
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static char[] toCharArray(final Object arr) {
		if (arr == null)
			return null;
		Class<?> type = arr.getClass();
		if (type.isArray()) {
			if (type.getComponentType() == Character.TYPE)
				return (char[]) arr;
			char[] ret = new char[Array.getLength(arr)];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = toChar(Array.get(arr, i));
			}
			return ret;
		}
		if (arr instanceof List) {
			return toCharArray((List) arr);
		}
		if (arr instanceof Collection) {
			return toCharArray((Collection) arr);
		}
		return new char[] { toChar(arr) };
	}

	// --- Boolean
	/**
	 * Converts the value to boolean.
	 * 
	 * @param value
	 *            may be a scalar, a collection or a array. Numeric values and Strings containing 1 or "true" are converted to true.
	 *            Everything else is converted to false
	 */
	public static boolean toBoolean(Object value) {
		value = getFirst(value);
		if (value == null) {
			return false;
		}
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof CharSequence) {
			CharSequence cs = (CharSequence) value;
			if (cs.length() == 0) {
				return false;
			}
			if (cs.length() == 4) {
				return Character.toLowerCase(cs.charAt(0)) == 't' && // 
						Character.toLowerCase(cs.charAt(1)) == 'r' && //
						Character.toLowerCase(cs.charAt(2)) == 'u' && //
						Character.toLowerCase(cs.charAt(3)) == 'e';
			}
			if (cs.length() >= 1) {
				return '1' <= cs.charAt(0) && cs.charAt(0) <= '9';
			}
			return false;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue() != 0;
		}

		throw new DataNotCompatibleException("Cannot convert a " + value.getClass().getName() + " to boolean primitive.");
	}

	/**
	 * Converts a Collection<?> to short[]
	 */
	public static <T> boolean[] toBooleanArray(final Collection<T> coll) {
		boolean[] ret = new boolean[coll.size()];
		Iterator<T> iterator = coll.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toBoolean(iterator.next());
		}
		return ret;
	}

	/**
	 * Converts a List<?> to short[]
	 */
	public static <T> boolean[] toBooleanArray(final List<T> list) {
		boolean[] ret = new boolean[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = toBoolean(list.get(i));
		}
		return ret;
	}

	/**
	 * Converts a Object to short[]
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean[] toBooleanArray(final Object arr) {
		if (arr == null)
			return null;
		Class<?> type = arr.getClass();
		if (type.isArray()) {
			if (type.getComponentType() == Boolean.TYPE)
				return (boolean[]) arr;
			boolean[] ret = new boolean[Array.getLength(arr)];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = toBoolean(Array.get(arr, i));
			}
			return ret;
		}
		if (arr instanceof List) {
			return toBooleanArray((List<?>) arr);
		}
		if (arr instanceof Collection) {
			return toBooleanArray((Collection) arr);
		}
		return new boolean[] { toBoolean(arr) };
	}

	public static Calendar parseIsoDate(final String image) {
		Calendar newCal = Calendar.getInstance();
		ParsePosition p = new ParsePosition(0);
		if (parseIsoDate(newCal, image, p))
			return newCal;
		throw new DataNotCompatibleException("The value '" + image + "' is not a valid ISO-Date");
	}

	public static boolean parseIsoDate(final Calendar newCal, final String image, final ParsePosition p) {
		newCal.setLenient(false);
		newCal.clear();
		p.setErrorIndex(-1);
		p.setIndex(0);
		DateFormat df = ISO.dateTimeFormat();
		df.parse(image, newCal, p);
		if (p.getErrorIndex() < 0)
			return true;
		if (image.length() >= 16) { // a valid time has 16 or more chars "1979-08-17T03:21"
			if (p.getErrorIndex() >= image.length())
				return true;
		}
		newCal.setLenient(false);
		newCal.clear();
		p.setErrorIndex(-1);
		p.setIndex(0);
		df = ISO.dateFormat();
		df.parse(image, newCal, p);
		if (p.getErrorIndex() < 0)
			return true;

		newCal.setLenient(false);
		newCal.clear();
		p.setErrorIndex(-1);
		p.setIndex(0);
		df = ISO.timeFormat();
		df.parse(image, newCal, p);
		if (p.getErrorIndex() < 0)
			return true;
		if (image.length() >= 4) { // a valid time has 4 or more chars "3:21"
			if (p.getErrorIndex() >= image.length())
				return true;
		}
		return false;
	}
}
