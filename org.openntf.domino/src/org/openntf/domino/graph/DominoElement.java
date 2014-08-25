package org.openntf.domino.graph;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.types.BigString;
import org.openntf.domino.types.CaseInsensitiveHashSet;
import org.openntf.domino.types.CaseInsensitiveString;
import org.openntf.domino.types.Null;
import org.openntf.domino.utils.Strings;

public abstract class DominoElement implements IDominoElement, Serializable {
	private static Map<Class<?>, Map<String, Method>> PROP_REFLECT_MAP = new ConcurrentHashMap<Class<?>, Map<String, Method>>();

	public static Object getReflectiveProperty(final IDominoElement element, final String prop) {
		if (prop == null || prop.isEmpty())
			return null;
		if (element.hasProperty(prop)) {
			//			System.out.println("Directory property found: " + prop);
			return element.getProperty(prop);
		} else {
			Class<?> cls = element.getClass();
			Map<String, Method> clsMap = PROP_REFLECT_MAP.get(cls);
			if (clsMap == null) {
				clsMap = new ConcurrentHashMap<String, Method>();
				synchronized (DominoElement.class) {
					PROP_REFLECT_MAP.put(cls, clsMap);
				}
			}
			Method crystal = clsMap.get(prop);
			if (crystal == null) {
				String getKey = "get" + Strings.toProperCase(prop);
				try {
					crystal = cls.getMethod(getKey, null);
				} catch (Exception e) {
					try {
						String isKey = "is" + Strings.toProperCase(prop);
						crystal = cls.getMethod(isKey, null);
					} catch (Exception e1) {
						//this is actually okay. It just happens we don't have a method for this thing.
					}
				}
				if (crystal != null) {
					synchronized (clsMap) {
						clsMap.put(prop, crystal);
						//						System.out.print("Caching method for " + crystal.getName());
					}
				}
			}
			if (crystal != null) {
				try {
					return crystal.invoke(element, null);
				} catch (Throwable t) {
					//TODO NTF should really replace the map with some kind of method reference that returns null in the future...
					System.out.println("Unable to discover a property " + prop + " through invoking a method called " + crystal.getName()
							+ " due to " + t.getMessage());
				}
			}
		}
		return null;
	}

	public static Object getReflectiveProperty(final IDominoElement element, final IDominoProperties prop) {
		return prop.getType().cast(getReflectiveProperty(element, prop.getName()));
	}

	private static final Logger log_ = Logger.getLogger(DominoElement.class.getName());
	private static final long serialVersionUID = 1L;
	public static final String TYPE_FIELD = "_OPEN_GRAPHTYPE";
	private String key_;
	protected transient DominoGraph parent_;
	private String unid_;
	private Map<CharSequence, Serializable> props_;
	public final String[] DEFAULT_STR_ARRAY = { "" };

	public static Document toDocument(final DominoElement element) {
		return element.getRawDocument();
	}

	public static enum Properties implements IDominoProperties {
		TITLE(String.class), KEY(String.class), FORM(String.class);

		private Class<?> type_;

		Properties(final Class<?> type) {
			type_ = type;
		}

		@Override
		public Class<?> getType() {
			return type_;
		}

		@Override
		public String getName() {
			return super.name();
		}
	}

	public DominoElement(final DominoGraph parent, final Document doc) {
		parent_ = parent;

		unid_ = doc.getUniversalID().toUpperCase();
	}

	private transient java.lang.Object lockHolder_;

	public synchronized boolean hasLock() {
		return lockHolder_ != null;
	}

	public synchronized boolean lock(final java.lang.Object lockHolder) {
		if (lockHolder_ == null) {
			lockHolder_ = lockHolder;
			return true;
		}
		return false;
	}

	public synchronized boolean unlock(final java.lang.Object lockHolder) {
		if (lockHolder.equals(lockHolder_)) {
			lockHolder_ = null;
			return true;
		}
		return false;
	}

	synchronized void unlock() {
		lockHolder_ = null;
	}

	public String getTitle() {
		return getProperty(Properties.TITLE, false);
	}

	public void setTitle(final String value) {
		setProperty(Properties.TITLE, value);
	}

	public String getKey() {
		return getProperty(Properties.KEY, false);
	}

	public void setKey(final String value) {
		setProperty(Properties.KEY, value);
	}

	public String getForm() {
		return getProperty(Properties.FORM, false);
	}

	public void setForm(final String value) {
		String current = getForm();
		if (current == null || !current.equalsIgnoreCase(value)) {
			setProperty(Properties.FORM, value);
		}
	}

	private Boolean isNew_;

	void setNew(final boolean isnew) {
		isNew_ = isnew;
	}

	public boolean isNew() {
		if (isNew_ == null) {
			isNew_ = false;
		}
		return isNew_;
	}

	private Map<CharSequence, Serializable> getProps() {
		if (props_ == null) {
			props_ = new ConcurrentHashMap<CharSequence, Serializable>();
		}
		return props_;
	}

	public void addProperty(final String propertyName, final Object value) {
		setProperty(propertyName, value);
	}

	private Database getDatabase() {
		return getParent().getRawDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof DominoElement) {
			return ((DominoElement) o).getId().equals(getId());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public Document getRawDocument() {
		return getDocument();
	}

	public String getRawId() {
		String prefix = getDatabase().getServer() + "!!" + getDatabase().getFilePath();
		return prefix + ": " + getRawDocument().getNoteID();
	}

	@Override
	public int incrementProperty(final String propertyName) {
		Integer result = getProperty(propertyName, Integer.class);
		if (result == null)
			result = 0;
		setProperty(propertyName, ++result);
		return result;
	}

	@Override
	public int decrementProperty(final String propertyName) {
		Integer result = getProperty(propertyName, Integer.class);
		if (result == null)
			result = 0;
		setProperty(propertyName, --result);
		return result;
	}

	private Document getDocument() {
		return getParent().getDocument(unid_, true);
		// Map<String, Document> map = documentCache.get();
		// Document doc = map.get(unid_);
		// if (doc == null) {
		// synchronized (map) {
		// doc = getDatabase().getDocumentByKey(unid_, true);
		// String localUnid = doc.getUniversalID().toUpperCase();
		// if (!unid_.equals(localUnid)) {
		// log_.log(Level.SEVERE, "UNIDs do not match! Expected: " + unid_ + ", Result: " + localUnid);
		// }
		// map.put(unid_, doc);
		// }
		// }
		// return doc;
	}

	@Override
	public String getId() {
		if (key_ == null) {
			key_ = unid_;
		}
		return key_;
	}

	public DominoGraph getParent() {
		return parent_;
	}

	@Override
	public boolean hasProperty(final String key) {
		return getPropertyKeys().contains(key);
	}

	@Override
	public <T> T getProperty(final String key) {
		return getProperty(key, java.lang.Object.class);
	}

	@Override
	public <T> T getProperty(final String propertyName, final Class<?> T) {
		Object result = null;
		CharSequence key = new CaseInsensitiveString(propertyName);
		Map<CharSequence, Serializable> props = getProps();
		// synchronized (props) {
		result = props.get(key);
		if (result == null) {
			//			if ("PROGNAME".equalsIgnoreCase(propertyName)) {
			//				System.out.println("DEBUG: " + propertyName + " getting from Document");
			//			}
			try {
				Document doc = getRawDocument();
				result = doc.getItemValue(propertyName, T);
				if (result == null) {
					synchronized (props) {
						props.put(key, Null.INSTANCE);
					}
				} else if (result instanceof Serializable) {
					synchronized (props) {
						props.put(key, (Serializable) result);
					}
				} else {
					log_.log(Level.WARNING, "Got a value from the document but it's not Serializable. It's a "
							+ result.getClass().getName());
				}
			} catch (Exception e) {
				log_.log(Level.WARNING, "Exception occured attempting to get value from document for " + propertyName
						+ " so we cannot return a value", e);
			}
		} else if (result == Null.INSTANCE) {

		} else {
			if (result != null && !T.isAssignableFrom(result.getClass())) {
				//				if ("PROGNAME".equalsIgnoreCase(propertyName)) {
				//					System.out.println("DEBUG: " + propertyName + " result is a " + result.getClass().getSimpleName());
				//				}
				// System.out.println("AH! We have the wrong type in the property cache! How did this happen?");
				try {
					Document doc = getRawDocument();
					result = doc.getItemValue(propertyName, T);
					if (result == null) {
						synchronized (props) {
							props.put(key, Null.INSTANCE);
						}
					} else if (result instanceof Serializable) {
						synchronized (props) {
							props.put(key, (Serializable) result);
						}
					}
				} catch (Exception e) {
					log_.log(Level.WARNING, "Exception occured attempting to get value from document for " + propertyName
							+ " but we have a value in the cache.", e);
				}
			} else {
				//				if ("PROGNAME".equalsIgnoreCase(propertyName)) {
				//					System.out.println("DEBUG: " + propertyName + " result is a " + result.getClass().getSimpleName());
				//				}
			}
		}
		// }
		//		if (result != null && !T.isAssignableFrom(result.getClass())) {
		//			log_.log(Level.WARNING, "Returning a " + result.getClass().getName() + " when we asked for a " + T.getName());
		//		}
		if (result == Null.INSTANCE) {
			result = null;
		}
		//		if ("PROGNAME".equalsIgnoreCase(propertyName)) {
		//			System.out.println("DEBUG: " + propertyName + " result is a " + (result == null ? "null" : result.getClass().getSimpleName()));
		//		}
		return (T) result;
	}

	@Override
	public <T> T getProperty(final String propertyName, final Class<?> T, final boolean allowNull) {
		T result = getProperty(propertyName, T);
		if (allowNull) {
			return result;
		} else {
			if (result == null || Null.INSTANCE == result) {
				if (T.isArray())
					if (T.getComponentType() == String.class) {
						return (T) DEFAULT_STR_ARRAY;
					} else {
						return (T) Array.newInstance(T.getComponentType(), 0);
					}
				if (Boolean.class.equals(T) || Boolean.TYPE.equals(T))
					return (T) Boolean.FALSE;
				if (Integer.class.equals(T) || Integer.TYPE.equals(T))
					return (T) Integer.valueOf(0);
				if (Long.class.equals(T) || Long.TYPE.equals(T))
					return (T) Long.valueOf(0l);
				if (Short.class.equals(T) || Short.TYPE.equals(T))
					return (T) Short.valueOf("0");
				if (Double.class.equals(T) || Double.TYPE.equals(T))
					return (T) Double.valueOf(0d);
				if (Float.class.equals(T) || Float.TYPE.equals(T))
					return (T) Float.valueOf(0f);
				if (String.class.equals(T))
					return (T) "";
				try {
					return (T) T.newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				return result;
			}
		}
	}

	@Override
	public Set<String> getPropertyKeys() {

		return getPropertyKeys(true);
	}

	private final Set<String> propKeys_ = Collections.synchronizedSet(new CaseInsensitiveHashSet());	//TODO MAKE THREAD SAFE!!;

	@Override
	public Set<String> getPropertyKeys(final boolean includeEdgeFields) {	//TODO NTF fix?
		if (propKeys_.isEmpty()) {
			propKeys_.addAll(getRawDocument().keySet());
		}
		if (includeEdgeFields) {
			return Collections.unmodifiableSet(propKeys_);
		} else {
			Set<String> result = new CaseInsensitiveHashSet();
			for (String name : propKeys_) {
				if (!(name.startsWith(DominoVertex.IN_PREFIX) || name.startsWith(DominoVertex.OUT_PREFIX))) {
					result.add(name);
				}
			}
			return Collections.unmodifiableSet(result);
		}
	}

	@Override
	public abstract void remove();

	//	{
	//		getParent().startTransaction(this);
	//		getRawDocument().removePermanently(true);
	//	}

	void _remove() {
		getParent().startTransaction(this);
		getRawDocument().removePermanently(true);
	}

	private final Set<String> removedProperties_ = Collections.synchronizedSet(new CaseInsensitiveHashSet());

	@Override
	public <T> T removeProperty(final String key) {
		getParent().startTransaction(this);
		T result = getProperty(key);
		Map<CharSequence, Serializable> props = getProps();
		synchronized (props) {
			props.remove(key);
		}
		Document doc = getRawDocument();
		synchronized (doc) {
			doc.removeItem(key);
		}
		synchronized (removedProperties_) {
			removedProperties_.add(key);
		}
		synchronized (propKeys_) {
			propKeys_.remove(key);
		}
		return result;
	}

	// public void save() {
	// getRawDocument().save();
	// }

	@Override
	public void setRawDocument(final org.openntf.domino.Document doc) {
		unid_ = doc.getUniversalID().toUpperCase();
	}

	private final Set<String> changedProperties_ = new CaseInsensitiveHashSet();

	//	void setProperty(final String propertyName, final java.lang.Object value, final boolean force) {
	//
	//	}

	@Override
	public void setProperty(final String propertyName, final java.lang.Object value) {
		//		if ("PROGNAME".equalsIgnoreCase(propertyName)) {
		//			System.out.println("DEBUG Setting " + propertyName);
		//		}
		boolean isEdgeCollection = false;
		boolean isEqual = false;
		CharSequence key = new CaseInsensitiveString(propertyName);
		Map<CharSequence, Serializable> props = getProps();
		Object old = null;
		if (props != null) {
			if (propertyName != null) {

				synchronized (propKeys_) {
					propKeys_.add(propertyName);
				}
				Object current = getProperty(propertyName);
				if (propertyName.startsWith(DominoVertex.IN_PREFIX) && value instanceof java.util.Collection) {
					isEdgeCollection = true;
				}
				if (current == null && value == null) {
					return;
				}
				if (value != null && current != null) {
					if (!(value instanceof java.util.Collection) && !(value instanceof java.util.Map) && !value.getClass().isArray()) {
						isEqual = value.equals(current);
					}
				}
				if (isEqual) {
					log_.log(Level.FINE, "Not setting property " + propertyName + " because the new value is equal to the existing value");
				}
				boolean changeMade = false;
				synchronized (props) {

					if (value instanceof Serializable) {
						if (current == null || Null.INSTANCE.equals(current)) {
							//							if ("PROGNAME".equalsIgnoreCase(propertyName)) {
							//								System.out.println("DEBUG: " + propertyName + " checking FROM NULL values from " + String.valueOf(current)
							//										+ " to " + String.valueOf(value));
							//							}
							getParent().startTransaction(this);
							old = props.put(key, (Serializable) value);
							synchronized (changedProperties_) {
								changedProperties_.add(propertyName);
							}
						} else if (!isEqual) {
							getParent().startTransaction(this);
							old = props.put(key, (Serializable) value);
							synchronized (changedProperties_) {
								changedProperties_.add(propertyName);
							}
						} else {
							//							if ("PROGNAME".equalsIgnoreCase(propertyName)) {
							//								System.out.println("DEBUG: " + propertyName + " equal?? values match from " + String.valueOf(current)
							//										+ " to " + String.valueOf(value));
							//							}
						}
					} else if (value == null) {
						if (!current.equals(Null.INSTANCE)) {
							getParent().startTransaction(this);
							old = props.put(key, Null.INSTANCE);
							synchronized (changedProperties_) {
								changedProperties_.add(propertyName);
							}
						}
					} else {
						//						if ("PROGNAME".equalsIgnoreCase(propertyName)) {
						//							System.out.println("DEBUG: " + propertyName + " values from " + String.valueOf(current) + " to "
						//									+ String.valueOf(value));
						//						}
						log_.log(Level.WARNING, "Attempted to set property " + propertyName + " to a non-serializable value: "
								+ value.getClass().getName());
					}
				}

			} else {
				log_.log(Level.WARNING, "propertyName is null on a setProperty request?");
			}
		} else {
			log_.log(Level.WARNING, "Properties are null for element!");
		}
	}

	protected void reapplyChanges() {
		Map<CharSequence, Serializable> props = getProps();
		Document doc = getDocument();
		synchronized (props) {
			if (props.isEmpty()) {
				// System.out.println("Cached properties is empty!");
			} else {
				synchronized (changedProperties_) {
					// System.out.println("Re-applying cached properties: " + changedProperties_.size());
					for (String s : changedProperties_) {
						CharSequence key = new CaseInsensitiveString(s);
						Object v = props.get(key);
						if (v == null) {
							// System.out.println("Writing a null value for property: " + key
							// + " to an Element document. Probably not good...");
						}
						if (s.startsWith(DominoVertex.IN_PREFIX) || s.startsWith(DominoVertex.OUT_PREFIX)) {
							doc.replaceItemValue(s, v, false);
						} else {
							doc.replaceItemValue(s, v);
						}
					}
					changedProperties_.clear();
				}

			}
		}
		synchronized (removedProperties_) {
			for (String key : removedProperties_) {
				doc.removeItem(key);
			}
		}
	}

	@Override
	public int incrementProperty(final IDominoProperties prop) {
		return incrementProperty(prop.getName());
	}

	@Override
	public int decrementProperty(final IDominoProperties prop) {
		return decrementProperty(prop.getName());
	}

	@Override
	public <T> T getProperty(final IDominoProperties prop) {
		if (prop == null) {
			log_.log(Level.WARNING, "getProperty was called with a null argument, therefore it's impossible to return a property.");
			return null;
		}
		Class<?> type = prop.getType();
		Object result = getProperty(prop.getName(), type);
		if (result != null && type.isAssignableFrom(result.getClass())) {
			return (T) type.cast(result);
		} else {
			// System.out.println("Property returned a " + (result == null ? "null" : result.getClass().getName())
			// + " even though we requested a " + type.getName());
		}
		return (T) result;
	}

	@Override
	public <T> T getProperty(final IDominoProperties prop, final boolean allowNull) {
		Class<?> type = prop.getType();
		Object result = getProperty(prop.getName(), type, allowNull);
		if (result != null && type.isAssignableFrom(result.getClass())) {
			return (T) type.cast(result);
		} else {
			// System.out.println("Property returned a " + (result == null ? "null" : result.getClass().getName())
			// + " even though we requested a " + type.getName());
		}
		return (T) result;
	}

	@Override
	public void setProperty(final IDominoProperties prop, final java.lang.Object value) {
		Object current = getProperty(prop, true);
		if (current == null || !current.equals(value)) {
			setProperty(prop.getName(), value);
		}
	}

	public static Object toMapValue(final Object value) {
		Object result = value;
		if (EnumSet.class.isAssignableFrom(value.getClass())) {
			System.out.println("DEBUG: Mapping an EnumSet");
			if (!((EnumSet) value).isEmpty()) {
				StringBuilder eListing = new StringBuilder();
				eListing.append('[');
				for (Object rawEnum : (EnumSet) value) {
					if (Enum.class.isAssignableFrom(rawEnum.getClass())) {
						eListing.append(((Enum) rawEnum).name());
					} else {
						eListing.append("ERROR: expected Enum was a " + rawEnum.getClass().getName());
					}
					eListing.append(',');
				}
				eListing.deleteCharAt(eListing.length() - 1);
				eListing.append(']');
				result = eListing.toString();
			} else {
				result = "";
			}
		} else if (Enum.class.isAssignableFrom(value.getClass())) {
			result = ((Enum) value).name();
		} else if (CharSequence.class.isAssignableFrom(value.getClass())) {
			result = ((CharSequence) value).toString();
		} else if (BigString.class.isAssignableFrom(value.getClass())) {
			result = ((BigString) value).toString();
		} else {
			result = value;
		}
		return result;
	}

	public Map<String, Object> toMap(final IDominoProperties[] props, final byte keyStyle) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		for (IDominoProperties prop : props) {
			String mapKey = prop.getName();
			if (keyStyle == Character.LOWERCASE_LETTER) {
				mapKey = mapKey.toLowerCase();
			} else if (keyStyle == Character.UPPERCASE_LETTER) {
				mapKey = mapKey.toUpperCase();
			}
			Object value = getProperty(prop, true);
			if (value != null) {
				result.put(mapKey, toMapValue(value));
			}
		}
		return result;
	}

	@Override
	public Map<String, Object> toMap(final IDominoProperties[] props) {
		return toMap(props, (byte) 0);
	}

	public Map<String, Object> toMap(final Set<IDominoProperties> props, final byte keyStyle) {
		return toMap(props.toArray(new IDominoProperties[props.size()]), keyStyle);
	}

	@Override
	public Map<String, Object> toMap(final Set<IDominoProperties> props) {
		return toMap(props, (byte) 0);
	}

}
