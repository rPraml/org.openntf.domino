/*
 * Copyright OpenNTF 2013
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
package org.openntf.domino.impl;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.NotesException;

import org.openntf.domino.DocumentCollection;
import org.openntf.domino.Item;
import org.openntf.domino.NoteCollection;
import org.openntf.domino.Session;
import org.openntf.domino.View;
import org.openntf.domino.annotations.Legacy;
import org.openntf.domino.exceptions.DataNotCompatibleException;
import org.openntf.domino.exceptions.ItemNotFoundException;
import org.openntf.domino.transactions.DatabaseTransaction;
import org.openntf.domino.utils.DominoUtils;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.TypeUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class Document.
 */
public class Document extends Base<org.openntf.domino.Document, lotus.domino.Document> implements org.openntf.domino.Document {
	private static final Logger log_ = Logger.getLogger(Document.class.getName());

	public static enum RemoveType {
		SOFT_FALSE, SOFT_TRUE, HARD_FALSE, HARD_TRUE;
	}

	private RemoveType removeType_;

	private boolean isDirty_ = false;
	private String noteid_;
	private boolean isQueued_ = false;
	private boolean isRemoveQueued_ = false;
	private boolean shouldWriteItemMeta_ = false; // TODO NTF create rules for making this true
	private boolean shouldResurrect_ = false;

	// NTF - these are immutable by definition, so we should just copy it when we read in the doc
	// yes, we're creating objects we might not need, but that's better than risking the toxicity of evil, wicked DateTime
	// these ought to be final, since they can't change, but it makes the constructor really messy

	// NTF - Okay, after testing, maybe these just need to be JIT getters. It added about 10% to Document iteration time.
	// NTF - Done. And yeah, it make quite a performance difference. More like 20%, really
	/** The created_. */
	private Date created_;

	/** The initially modified_. */
	private Date initiallyModified_;

	/** The last modified_. */
	private Date lastModified_;

	/** The last accessed_. */
	private Date lastAccessed_;

	/**
	 * Instantiates a new document.
	 * 
	 * @param delegate
	 *            the delegate
	 * @param parent
	 *            the parent
	 */
	public Document(lotus.domino.Document delegate, org.openntf.domino.Base<?> parent) {
		super(delegate, Factory.getParentDatabase(parent));
		initialize(delegate);
	}

	/**
	 * Initialize.
	 * 
	 * @param delegate
	 *            the delegate
	 */
	@SuppressWarnings("unused")
	private void initialize(lotus.domino.Document delegate) {
		try {
			// delegate.setPreferJavaDates(true);
			noteid_ = delegate.getNoteID();
			// created_ = DominoUtils.toJavaDateSafe(delegate.getCreated());
			// initiallyModified_ = DominoUtils.toJavaDateSafe(delegate.getInitiallyModified());
			// lastModified_ = DominoUtils.toJavaDateSafe(delegate.getLastModified());
			// lastAccessed_ = DominoUtils.toJavaDateSafe(delegate.getLastAccessed());
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getCreated()
	 */
	@Override
	@Deprecated
	@Legacy(Legacy.DATETIME_WARNING)
	public org.openntf.domino.DateTime getCreated() {
		try {
			// if (created_ == null) {
			// created_ = DominoUtils.toJavaDateSafe(getDelegate().getCreated());
			// }
			return new DateTime(getDelegate().getCreated(), this); // TODO NTF - maybe ditch the parent?
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getCreatedDate()
	 */
	public Date getCreatedDate() {
		if (created_ == null) {
			try {
				created_ = DominoUtils.toJavaDateSafe(getDelegate().getCreated());
			} catch (NotesException e) {
				DominoUtils.handleException(e);
			}
		}
		return created_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getInitiallyModified()
	 */
	@Override
	@Deprecated
	@Legacy(Legacy.DATETIME_WARNING)
	public DateTime getInitiallyModified() {
		try {
			// if (initiallyModified_ == null) {
			// initiallyModified_ = DominoUtils.toJavaDateSafe(getDelegate().getInitiallyModified());
			// }
			return new DateTime(getDelegate().getInitiallyModified(), this); // TODO NTF - maybe ditch the parent?

		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getInitiallyModifiedDate()
	 */
	public Date getInitiallyModifiedDate() {
		if (initiallyModified_ == null) {
			try {
				initiallyModified_ = DominoUtils.toJavaDateSafe(getDelegate().getInitiallyModified());
			} catch (NotesException e) {
				DominoUtils.handleException(e);

			}
		}
		return initiallyModified_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getLastAccessed()
	 */
	@Override
	@Deprecated
	@Legacy(Legacy.DATETIME_WARNING)
	public DateTime getLastAccessed() {
		try {
			// if (lastAccessed_ == null) {
			// lastAccessed_ = DominoUtils.toJavaDateSafe(getDelegate().getLastAccessed());
			// }
			return new DateTime(getDelegate().getLastAccessed(), this); // TODO NTF - maybe ditch the parent?

		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getLastAccessedDate()
	 */
	public Date getLastAccessedDate() {
		if (lastAccessed_ == null) {
			try {
				lastAccessed_ = DominoUtils.toJavaDateSafe(getDelegate().getLastAccessed());
			} catch (NotesException e) {
				DominoUtils.handleException(e);

			}
		}
		return lastAccessed_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getLastModified()
	 */
	@Override
	@Deprecated
	@Legacy(Legacy.DATETIME_WARNING)
	public DateTime getLastModified() {
		try {
			// if (lastModified_ == null) {
			// lastModified_ = DominoUtils.toJavaDateSafe(getDelegate().getLastModified());
			// }
			return new DateTime(getDelegate().getLastModified(), this); // TODO NTF - maybe ditch the parent?

		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getLastModifiedDate()
	 */
	public Date getLastModifiedDate() {
		if (lastModified_ == null) {
			try {
				lastModified_ = DominoUtils.toJavaDateSafe(getDelegate().getLastModified());
			} catch (NotesException e) {
				DominoUtils.handleException(e);

			}
		}
		return lastModified_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#appendItemValue(java.lang.String)
	 */
	@Override
	public Item appendItemValue(String name) {
		markDirty();
		try {
			return Factory.fromLotus(getDelegate().appendItemValue(name), Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#appendItemValue(java.lang.String, double)
	 */
	@Override
	public Item appendItemValue(String name, double value) {
		markDirty();
		try {
			return Factory.fromLotus(getDelegate().appendItemValue(name, value), Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#appendItemValue(java.lang.String, int)
	 */
	@Override
	public Item appendItemValue(String name, int value) {
		markDirty();
		try {
			return Factory.fromLotus(getDelegate().appendItemValue(name, value), Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#appendItemValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public Item appendItemValue(String name, Object value) {
		markDirty();
		try {
			if (value instanceof org.openntf.domino.DateTime) {
				value = toLotus((org.openntf.domino.DateTime) value);
			} else if (value instanceof org.openntf.domino.DateRange) {
				value = toLotus((org.openntf.domino.DateRange) value);
			}
			return Factory.fromLotus(getDelegate().appendItemValue(name, value), Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#attachVCard(lotus.domino.Base)
	 */
	@Override
	public void attachVCard(lotus.domino.Base document) {
		markDirty();
		try {
			getDelegate().attachVCard(toLotus(document));
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#attachVCard(lotus.domino.Base, java.lang.String)
	 */
	@Override
	public void attachVCard(lotus.domino.Base document, String charset) {
		markDirty();
		try {
			getDelegate().attachVCard(toLotus(document), charset);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#closeMIMEEntities()
	 */
	@Override
	public boolean closeMIMEEntities() {
		try {
			return getDelegate().closeMIMEEntities();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#closeMIMEEntities(boolean)
	 */
	@Override
	public boolean closeMIMEEntities(boolean saveChanges) {
		try {
			return getDelegate().closeMIMEEntities(saveChanges);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#closeMIMEEntities(boolean, java.lang.String)
	 */
	@Override
	public boolean closeMIMEEntities(boolean saveChanges, String entityItemName) {
		try {
			return getDelegate().closeMIMEEntities(saveChanges, entityItemName);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#computeWithForm(boolean, boolean)
	 */
	@Override
	public boolean computeWithForm(boolean doDataTypes, boolean raiseError) {
		markDirty();
		try {
			return getDelegate().computeWithForm(doDataTypes, raiseError);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#convertToMIME()
	 */
	@Override
	public void convertToMIME() {
		try {
			getDelegate().convertToMIME();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#convertToMIME(int)
	 */
	@Override
	public void convertToMIME(int conversionType) {
		try {
			getDelegate().convertToMIME(conversionType);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#convertToMIME(int, int)
	 */
	@Override
	public void convertToMIME(int conversionType, int options) {
		try {
			getDelegate().convertToMIME(conversionType, options);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#copyAllItems(lotus.domino.Document, boolean)
	 */
	@Override
	public void copyAllItems(lotus.domino.Document doc, boolean replace) {
		try {
			getDelegate().copyAllItems((lotus.domino.Document) toLotus(doc), replace);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#copyItem(lotus.domino.Item)
	 */
	@Override
	public Item copyItem(lotus.domino.Item item) {
		// TODO - NTF markDirty()?
		try {
			return Factory.fromLotus(getDelegate().copyItem((lotus.domino.Item) toLotus(item)), Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#copyItem(lotus.domino.Item, java.lang.String)
	 */
	@Override
	public Item copyItem(lotus.domino.Item item, String newName) {
		// TODO - NTF markDirty()?
		try {
			return Factory.fromLotus(getDelegate().copyItem((lotus.domino.Item) toLotus(item), newName), Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#copyToDatabase(lotus.domino.Database)
	 */
	@Override
	public Document copyToDatabase(lotus.domino.Database db) {
		// TODO - NTF markDirty()?
		try {
			return Factory.fromLotus(getDelegate().copyToDatabase((lotus.domino.Database) toLotus(db)), Document.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#createMIMEEntity()
	 */
	@Override
	public MIMEEntity createMIMEEntity() {
		markDirty();
		try {
			return Factory.fromLotus(getDelegate().createMIMEEntity(), MIMEEntity.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	private final transient Map<String, MIMEEntity> entityCache_ = new HashMap<String, MIMEEntity>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#createMIMEEntity(java.lang.String)
	 */
	@Override
	public MIMEEntity createMIMEEntity(String itemName) {
		// if (entityCache_.containsKey(itemName)) {
		// log_.warning("Returning MIMEEntity for " + itemName + " from cache instead of creating...");
		// return entityCache_.get(itemName);
		// }
		try {
			try {
				lotus.domino.MIMEEntity me = getDelegate().createMIMEEntity(itemName);
				markDirty();
				MIMEEntity wrapped = Factory.fromLotus(me, MIMEEntity.class, this);
				// entityCache_.put(itemName, wrapped);
				return wrapped;
			} catch (NotesException alreadyThere) {
				Item item = getFirstItem(itemName);
				if (item != null) {
					log_.warning("Already have a non-MIME item for " + itemName + ". Removing...");
					item.remove();
				}
				lotus.domino.MIMEEntity me = getDelegate().createMIMEEntity(itemName);
				markDirty();
				MIMEEntity wrapped = Factory.fromLotus(me, MIMEEntity.class, this);
				// entityCache_.put(itemName, wrapped);
				return wrapped;
			}
			// return Factory.fromLotus(getDelegate().createMIMEEntity(itemName), MIMEEntity.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#createReplyMessage(boolean)
	 */
	@Override
	public Document createReplyMessage(boolean toAll) {
		// TODO - NTF markDirty()?
		try {
			return Factory.fromLotus(getDelegate().createReplyMessage(toAll), Document.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#createRichTextItem(java.lang.String)
	 */
	@Override
	public RichTextItem createRichTextItem(String name) {
		markDirty();
		try {
			return Factory.fromLotus(getDelegate().createRichTextItem(name), RichTextItem.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#encrypt()
	 */
	@Override
	public void encrypt() {
		markDirty();
		try {
			getDelegate().encrypt();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#generateXML()
	 */
	@Override
	public String generateXML() {
		try {
			return getDelegate().generateXML();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#generateXML(java.lang.Object, lotus.domino.XSLTResultTarget)
	 */
	@Override
	public void generateXML(Object style, lotus.domino.XSLTResultTarget result) throws IOException {
		try {
			getDelegate().generateXML(style, result);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#generateXML(java.io.Writer)
	 */
	@Override
	public void generateXML(Writer w) throws IOException {
		try {
			getDelegate().generateXML(w);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getAttachment(java.lang.String)
	 */
	@Override
	public EmbeddedObject getAttachment(String fileName) {
		try {
			return Factory.fromLotus(getDelegate().getAttachment(fileName), EmbeddedObject.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getAuthors()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Vector<String> getAuthors() {
		try {
			return getDelegate().getAuthors();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getColumnValues()
	 */
	@Override
	public Vector<Object> getColumnValues() {
		try {
			Vector<?> values = getDelegate().getColumnValues();
			if (values != null) {
				return Factory.wrapColumnValues(values, this.getAncestorSession());
			} else {
				return null;
			}
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getEmbeddedObjects()
	 */
	@Override
	public Vector<org.openntf.domino.EmbeddedObject> getEmbeddedObjects() {
		try {
			return Factory.fromLotusAsVector(getDelegate().getEmbeddedObjects(), org.openntf.domino.EmbeddedObject.class,
					this.getAncestorSession());
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getEncryptionKeys()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Vector<String> getEncryptionKeys() {
		try {
			return getDelegate().getEncryptionKeys();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getFTSearchScore()
	 */
	@Override
	public int getFTSearchScore() {
		try {
			return getDelegate().getFTSearchScore();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getFirstItem(java.lang.String)
	 */
	@Override
	public Item getFirstItem(String name) {
		try {
			return Factory.fromLotus(getDelegate().getFirstItem(name), Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getFolderReferences()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Vector<String> getFolderReferences() {
		try {
			return getDelegate().getFolderReferences();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getHttpURL()
	 */
	@Override
	public String getHttpURL() {
		try {
			return getDelegate().getHttpURL();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * Behavior: If the document does not have the item, then we look at the requested class. If it's a primitive or an array of primitives,
	 * we cannot return a null value that can be assigned to that type, so therefore we throw an Exception. If what was request is an
	 * object, we return null.
	 * 
	 * If the item does exist, then we get it's value and attempt a conversion. If the data cannot be converted, we throw an Exception
	 */

	public <T> T getItemValue(String name, Class<?> T) throws ItemNotFoundException, DataNotCompatibleException {
		// TODO NTF - Add type conversion extensibility of some kind, maybe attached to the Database or the Session
		return TypeUtils.itemValueToClass(this, name, T);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getItemValue(java.lang.String)
	 */
	@Override
	public Vector<Object> getItemValue(String name) {
		try {
			// Check the item type to see if it's MIME - if so, then see if it's a MIMEBean
			// This is a bit more expensive than I'd like
			lotus.domino.Session session = this.getAncestorSession();
			boolean convertMime = session.isConvertMIME();
			session.setConvertMIME(false);
			Item item = this.getFirstItem(name);
			if (item.getType() == Item.MIME_PART) {
				MIMEEntity entity = this.getMIMEEntity(name);
				MIMEHeader contentType = entity.getNthHeader("Content-Type");
				if (contentType != null
						&& (contentType.getHeaderVal().equals("application/x-java-serialized-object") || contentType.getHeaderVal().equals(
								"application/x-java-externalized-object"))) {
					// Then it's a MIMEBean
					Object resultObj = DominoUtils.restoreState(this, name);

					// If it's a List, return it - otherwise, store it in a Vector for consistency
					if (resultObj instanceof List) {
						return new java.util.Vector<Object>((List<?>) resultObj);
					}
					Vector<Object> result = new Vector<Object>(1);
					result.add(resultObj);
					session.setConvertMIME(convertMime);
					return result;
				}
			}
			session.setConvertMIME(convertMime);
			return Factory.wrapColumnValues(getDelegate().getItemValue(name), this.getAncestorSession());
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		} catch (Throwable t) {
			// From DominoUtils.restoreState(...)
			DominoUtils.handleException(t);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getItemValueCustomData(java.lang.String)
	 */
	@Override
	public Object getItemValueCustomData(String itemName) throws IOException, ClassNotFoundException {
		try {
			return getDelegate().getItemValueCustomData(itemName);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getItemValueCustomData(java.lang.String, java.lang.String)
	 */
	@Override
	public Object getItemValueCustomData(String itemName, String dataTypeName) throws IOException, ClassNotFoundException {
		try {
			return getDelegate().getItemValueCustomData(itemName, dataTypeName);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getItemValueCustomDataBytes(java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] getItemValueCustomDataBytes(String itemName, String dataTypeName) throws IOException {
		try {
			return getDelegate().getItemValueCustomDataBytes(itemName, dataTypeName);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getItemValueDateTimeArray(java.lang.String)
	 */
	@Override
	public Vector<org.openntf.domino.DateTime> getItemValueDateTimeArray(String name) {
		try {
			return Factory.fromLotusAsVector(getDelegate().getItemValueDateTimeArray(name), org.openntf.domino.DateTime.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getItemValueDouble(java.lang.String)
	 */
	@Override
	public double getItemValueDouble(String name) {
		try {
			return getDelegate().getItemValueDouble(name);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return 0d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getItemValueInteger(java.lang.String)
	 */
	@Override
	public int getItemValueInteger(String name) {
		try {
			return getDelegate().getItemValueInteger(name);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getItemValueString(java.lang.String)
	 */
	@Override
	public String getItemValueString(String name) {
		try {
			return getDelegate().getItemValueString(name);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getItems()
	 */
	@Override
	public Vector<Item> getItems() {
		try {
			return Factory.fromLotusAsVector(getDelegate().getItems(), Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getKey()
	 */
	@Override
	public String getKey() {
		try {
			return getDelegate().getKey();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getLockHolders()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Vector<String> getLockHolders() {
		try {
			return getDelegate().getLockHolders();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getMIMEEntity()
	 */
	@Override
	public MIMEEntity getMIMEEntity() {
		try {
			return Factory.fromLotus(getDelegate().getMIMEEntity(), MIMEEntity.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getMIMEEntity(java.lang.String)
	 */
	@Override
	public MIMEEntity getMIMEEntity(String itemName) {
		// if (entityCache_.containsKey(itemName)) {
		// log_.warning("Returning MIMEEntity " + itemName + " from local document cache...");
		// return entityCache_.get(itemName);
		// }
		try {
			MIMEEntity wrapped = Factory.fromLotus(getDelegate().getMIMEEntity(itemName), MIMEEntity.class, this);
			// entityCache_.put(itemName, wrapped);
			return wrapped;
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getNameOfProfile()
	 */
	@Override
	public String getNameOfProfile() {
		try {
			return getDelegate().getNameOfProfile();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getNoteID()
	 */
	@Override
	public String getNoteID() {
		try {
			return getDelegate().getNoteID();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getNotesURL()
	 */
	@Override
	public String getNotesURL() {
		try {
			return getDelegate().getNotesURL();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getParentDatabase()
	 */
	@Override
	public Database getParentDatabase() {
		return (Database) super.getParent();
	}

	public Document getParentDocument() {
		return this.getParentDatabase().getDocumentByUNID(this.getParentDocumentUNID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getParentDocumentUNID()
	 */
	@Override
	public String getParentDocumentUNID() {
		try {
			return getDelegate().getParentDocumentUNID();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getParentView()
	 */
	@Override
	public View getParentView() {
		try {
			return Factory.fromLotus(getDelegate().getParentView(), View.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getRead()
	 */
	@Override
	public boolean getRead() {
		try {
			return getDelegate().getRead();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getRead(java.lang.String)
	 */
	@Override
	public boolean getRead(String userName) {
		try {
			return getDelegate().getRead(userName);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getReceivedItemText()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Vector<String> getReceivedItemText() {
		try {
			return getDelegate().getReceivedItemText();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getResponses()
	 */
	@Override
	public DocumentCollection getResponses() {
		try {
			return Factory.fromLotus(getDelegate().getResponses(), DocumentCollection.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getSigner()
	 */
	@Override
	public String getSigner() {
		try {
			return getDelegate().getSigner();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getSize()
	 */
	@Override
	public int getSize() {
		try {
			return getDelegate().getSize();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getURL()
	 */
	@Override
	public String getURL() {
		try {
			return getDelegate().getURL();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getUniversalID()
	 */
	@Override
	public String getUniversalID() {
		try {
			return getDelegate().getUniversalID();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#getVerifier()
	 */
	@Override
	public String getVerifier() {
		try {
			return getDelegate().getVerifier();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#hasEmbedded()
	 */
	@Override
	public boolean hasEmbedded() {
		try {
			return getDelegate().hasEmbedded();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#hasItem(java.lang.String)
	 */
	@Override
	public boolean hasItem(String name) {
		try {
			if (name == null) {
				return false;
			} else {
				return getDelegate().hasItem(name);
			}
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isDeleted()
	 */
	@Override
	public boolean isDeleted() {
		try {
			return getDelegate().isDeleted();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isEncryptOnSend()
	 */
	@Override
	public boolean isEncryptOnSend() {
		try {
			return getDelegate().isEncryptOnSend();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isEncrypted()
	 */
	@Override
	public boolean isEncrypted() {
		try {
			return getDelegate().isEncrypted();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isNewNote()
	 */
	@Override
	public boolean isNewNote() {
		try {
			return getDelegate().isNewNote();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isPreferJavaDates()
	 */
	@Override
	public boolean isPreferJavaDates() {
		try {
			return getDelegate().isPreferJavaDates();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isProfile()
	 */
	@Override
	public boolean isProfile() {
		try {
			return getDelegate().isProfile();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isResponse()
	 */
	@Override
	public boolean isResponse() {
		try {
			return getDelegate().isResponse();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isSaveMessageOnSend()
	 */
	@Override
	public boolean isSaveMessageOnSend() {
		try {
			return getDelegate().isSaveMessageOnSend();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isSentByAgent()
	 */
	@Override
	public boolean isSentByAgent() {
		try {
			return getDelegate().isSentByAgent();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isSignOnSend()
	 */
	@Override
	public boolean isSignOnSend() {
		try {
			return getDelegate().isSignOnSend();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isSigned()
	 */
	@Override
	public boolean isSigned() {
		try {
			return getDelegate().isSigned();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#isValid()
	 */
	@Override
	public boolean isValid() {
		try {
			return getDelegate().isValid();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#lock()
	 */
	@Override
	public boolean lock() {
		try {
			return getDelegate().lock();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#lock(boolean)
	 */
	@Override
	public boolean lock(boolean provisionalOk) {
		try {
			return getDelegate().lock(provisionalOk);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#lock(java.lang.String)
	 */
	@Override
	public boolean lock(String name) {
		try {
			return getDelegate().lock(name);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#lock(java.lang.String, boolean)
	 */
	@Override
	public boolean lock(String name, boolean provisionalOk) {
		try {
			return getDelegate().lock(name, provisionalOk);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#lock(java.util.Vector)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean lock(Vector names) {
		try {
			return getDelegate().lock(names);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#lock(java.util.Vector, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean lock(Vector names, boolean provisionalOk) {
		try {
			return getDelegate().lock(names, provisionalOk);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#lockProvisional()
	 */
	@Override
	public boolean lockProvisional() {
		try {
			return getDelegate().lockProvisional();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#lockProvisional(java.lang.String)
	 */
	@Override
	public boolean lockProvisional(String name) {
		try {
			return getDelegate().lockProvisional(name);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#lockProvisional(java.util.Vector)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean lockProvisional(Vector names) {
		try {
			return getDelegate().lockProvisional(names);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#makeResponse(lotus.domino.Document)
	 */
	@Override
	public void makeResponse(lotus.domino.Document doc) {
		markDirty();
		try {
			getDelegate().makeResponse((lotus.domino.Document) toLotus(doc));
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#markRead()
	 */
	@Override
	public void markRead() {
		// TODO - NTF transaction context?
		try {
			getDelegate().markRead();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#markRead(java.lang.String)
	 */
	@Override
	public void markRead(String userName) {
		// TODO - NTF transaction context?
		try {
			getDelegate().markRead(userName);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#markUnread()
	 */
	@Override
	public void markUnread() {
		// TODO - NTF transaction context?
		try {
			getDelegate().markUnread();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#markUnread(java.lang.String)
	 */
	@Override
	public void markUnread(String userName) {
		// TODO - NTF transaction context?
		try {
			getDelegate().markUnread(userName);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#putInFolder(java.lang.String)
	 */
	@Override
	public void putInFolder(String name) {
		// TODO - NTF handle transaction context
		try {
			getDelegate().putInFolder(name);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#putInFolder(java.lang.String, boolean)
	 */
	@Override
	public void putInFolder(String name, boolean createOnFail) {
		// TODO - NTF handle transaction context
		try {
			getDelegate().putInFolder(name, createOnFail);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#remove(boolean)
	 */
	@Override
	public boolean remove(boolean force) {
		removeType_ = force ? RemoveType.SOFT_TRUE : RemoveType.SOFT_FALSE;
		if (!queueRemove()) {
			return forceDelegateRemove();
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#removeFromFolder(java.lang.String)
	 */
	@Override
	public void removeFromFolder(String name) {
		// TODO - NTF handle transaction context
		try {
			getDelegate().removeFromFolder(name);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#removeItem(java.lang.String)
	 */
	@Override
	public void removeItem(String name) {
		markDirty();
		try {
			getDelegate().removeItem(name);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#removePermanently(boolean)
	 */
	@Override
	public boolean removePermanently(boolean force) {
		removeType_ = force ? RemoveType.HARD_TRUE : RemoveType.HARD_FALSE;
		if (!queueRemove()) {
			return forceDelegateRemove();
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#renderToRTItem(lotus.domino.RichTextItem)
	 */
	@Override
	public boolean renderToRTItem(lotus.domino.RichTextItem rtitem) {
		try {
			getDelegate().renderToRTItem((lotus.domino.RichTextItem) toLotus(rtitem));
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#replaceItemValue(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Item replaceItemValue(final String itemName, Object value) {
		if (value == null)
			value = "";
		markDirty();
		try {
			lotus.domino.Item result = null;
			Class<?> valueClass = value.getClass();

			try {
				if (value instanceof List) {
					Vector<Object> resultList = new Vector<Object>();
					Class<?> objectClass = null;
					long totalStringSize = 0;
					for (Object valNode : (List<?>) value) {
						Object domNode = toDominoFriendly(valNode, this);
						if (objectClass == null) {
							objectClass = domNode.getClass();
						} else {
							if (!objectClass.equals(domNode.getClass())) {
								// Domino only allows uniform lists
								throw new IllegalArgumentException();
							}
						}
						if (domNode instanceof String) {
							totalStringSize += ((String) domNode).length();

							// Escape to serializing if there's too much text data
							// Leave fudge room for multibyte? This is clearly not the best way to do it
							if (totalStringSize > 60000) {
								throw new IllegalArgumentException();
							}
						}
						resultList.add(domNode);
						enc_recycle(domNode);
					}
					// If it ended up being something we could store, make note of the original class instead of the list class
					valueClass = ((List<?>) value).get(0).getClass();
					MIMEEntity mimeChk = getMIMEEntity(itemName);
					if (mimeChk != null) {
						mimeChk.remove();
					}
					result = getDelegate().replaceItemValue(itemName, resultList);
				} else {
					Object domNode = toDominoFriendly(value, this);
					if (domNode instanceof String && ((String) domNode).length() > 60000) {
						throw new IllegalArgumentException();
					}
					try {
						MIMEEntity mimeChk = getMIMEEntity(itemName);
						if (mimeChk != null) {

							mimeChk.remove();
						}
						result = getDelegate().replaceItemValue(itemName, domNode);
					} catch (NotesException nativeError) {
						log_.warning("Native error occured when replacing " + itemName + " item on doc " + this.noteid_
								+ " with a value of type " + (domNode == null ? "null" : domNode.getClass().getName()) + " of value "
								+ String.valueOf(domNode));
					}
					Base.enc_recycle(domNode);
				}
			} catch (IllegalArgumentException iae) {
				// Then try serialization
				if (value instanceof Serializable) {
					DominoUtils.saveState((Serializable) value, this, itemName);
					result = getDelegate().getFirstItem(itemName);
				} else if (value instanceof DocumentCollection) {
					// NoteIDs would be faster for this and, particularly, NoteCollection, but it should be replica-friendly
					DocumentCollection docs = (DocumentCollection) value;
					String[] unids = new String[docs.getCount()];
					int index = 0;
					for (org.openntf.domino.Document doc : docs) {
						unids[index++] = doc.getUniversalID();
					}
					Map<String, String> headers = new HashMap<String, String>(1);
					headers.put("X-Original-Java-Class", "org.openntf.domino.DocumentCollection");
					DominoUtils.saveState(unids, this, itemName, true, headers);
					result = getDelegate().getFirstItem(itemName);
				} else if (value instanceof NoteCollection) {
					// Maybe it'd be faster to use .getNoteIDs - I'm not sure how the performance compares
					NoteCollection notes = (NoteCollection) value;
					String[] unids = new String[notes.getCount()];
					String noteid = notes.getFirstNoteID();
					int index = 0;
					while (noteid != null && !noteid.isEmpty()) {
						unids[index++] = notes.getUNID(noteid);
						noteid = notes.getNextNoteID(noteid);
					}
					Map<String, String> headers = new HashMap<String, String>(1);
					headers.put("X-Original-Java-Class", "org.openntf.domino.NoteCollection");
					DominoUtils.saveState(unids, this, itemName, true, headers);
					result = getDelegate().getFirstItem(itemName);
				} else {
					// Check to see if it's a StateHolder
					try {
						Class<?> stateHolderClass = Class.forName("javax.faces.component.StateHolder");
						if (stateHolderClass.isInstance(value)) {
							Class<?> facesContextClass = Class.forName("javax.faces.context.FacesContext");
							Method getCurrentInstance = facesContextClass.getMethod("getCurrentInstance");
							Method saveState = stateHolderClass.getMethod("saveState", facesContextClass);
							Serializable state = (Serializable) saveState.invoke(value, getCurrentInstance.invoke(null));
							Map<String, String> headers = new HashMap<String, String>();
							headers.put("X-Storage-Scheme", "StateHolder");
							headers.put("X-Original-Java-Class", value.getClass().getName());
							DominoUtils.saveState(state, this, itemName, true, headers);
							result = getDelegate().getFirstItem(itemName);
						} else {
							// Well, we tried.
							throw iae;
						}
					} catch (ClassNotFoundException cnfe) {
						throw iae;
					}
				}
			}

			if (this.shouldWriteItemMeta_) {
				// If we've gotten this far, it must be legal - update or create the item info map

				Map<String, Map<String, Serializable>> itemInfo = getItemInfo();
				Map<String, Serializable> infoNode = null;
				if (itemInfo.containsKey(itemName)) {
					infoNode = itemInfo.get(itemName);
				} else {
					infoNode = new HashMap<String, Serializable>();
				}
				infoNode.put("valueClass", valueClass.getName());
				infoNode.put("updated", new Date()); // For sanity checking if the value was changed outside of Java
				itemInfo.put(itemName, infoNode);
			}
			return Factory.fromLotus(result, Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		} catch (Throwable t) {
			DominoUtils.handleException(t);
		}
		return null;
	}

	private void writeItemInfo() {
		if (this.shouldWriteItemMeta_) {
			Map<String, Map<String, Serializable>> itemInfo = getItemInfo();
			if (itemInfo != null && itemInfo.size() > 0) {
				boolean convertMime = this.getAncestorSession().isConvertMime();
				this.getAncestorSession().setConvertMime(false);
				try {
					DominoUtils.saveState((Serializable) getItemInfo(), this, "$$ItemInfo", false, null);
				} catch (Throwable e) {
					DominoUtils.handleException(e);
				}
				this.getAncestorSession().setConvertMime(convertMime);
			}
		}
	}

	private Map<String, Map<String, Serializable>> itemInfo_;

	public Map<String, Map<String, Serializable>> getItemInfo() {
		// TODO NTF make this optional
		if (itemInfo_ == null) {
			if (this.hasItem("$$ItemInfo")) {
				if (this.getFirstItem("$$ItemInfo").getType() == Item.MIME_PART) {
					// Then use the existing value
					try {
						itemInfo_ = (Map<String, Map<String, Serializable>>) DominoUtils.restoreState(this, "$$ItemInfo");
					} catch (Throwable t) {
						DominoUtils.handleException(t);
					}
				} else {
					// Then destroy it (?)
					this.removeItem("$$ItemInfo");
					itemInfo_ = new TreeMap<String, Map<String, Serializable>>();
				}
			} else {
				itemInfo_ = new TreeMap<String, Map<String, Serializable>>();
			}
		}
		return itemInfo_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#replaceItemValueCustomData(java.lang.String, java.lang.Object)
	 */
	@Override
	public Item replaceItemValueCustomData(String itemName, Object userObj) throws IOException {
		markDirty();
		try {
			return Factory.fromLotus(getDelegate().replaceItemValueCustomData(itemName, userObj), Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#replaceItemValueCustomData(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public Item replaceItemValueCustomData(String itemName, String dataTypeName, Object userObj) throws IOException {
		markDirty();
		try {
			return Factory.fromLotus(getDelegate().replaceItemValueCustomData(itemName, dataTypeName, userObj), Item.class, this);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#replaceItemValueCustomDataBytes(java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public Item replaceItemValueCustomDataBytes(String itemName, String dataTypeName, byte[] byteArray) throws IOException {
		markDirty();
		try {
			if (byteArray.length > 65535) {
				// Then fall back to the normal method, which will MIMEBean it
				return this.replaceItemValue(itemName, byteArray);
			} else {
				return Factory
						.fromLotus(getDelegate().replaceItemValueCustomDataBytes(itemName, dataTypeName, byteArray), Item.class, this);
			}
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#save()
	 */
	@Override
	public boolean save() {
		boolean result = false;
		result = save(false, false, false);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#save(boolean)
	 */
	@Override
	public boolean save(boolean force) {
		boolean result = false;
		result = save(force, false, false);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#save(boolean, boolean)
	 */
	@Override
	public boolean save(boolean force, boolean makeResponse) {
		boolean result = false;
		result = save(force, makeResponse, false);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#save(boolean, boolean, boolean)
	 */
	@Override
	public boolean save(boolean force, boolean makeResponse, boolean markRead) {
		boolean result = false;
		if (isDirty()) {
			writeItemInfo();
			try {
				result = getDelegate().save(force, makeResponse, markRead);
				if (result)
					clearDirty();
			} catch (NotesException e) {
				DominoUtils.handleException(e);
			}
		} else {
			if (log_.isLoggable(Level.FINE)) {
				log_.log(Level.FINE, "Document " + getNoteID() + " was not saved because nothing on it was changed.");
			}
			result = true; // because nothing changed, we don't want to activate any potential failure behavior in the caller
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#send()
	 */
	@Override
	public void send() {
		// TODO - NTF handle transaction context
		try {
			getDelegate().send();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#send(boolean)
	 */
	@Override
	public void send(boolean attachForm) {
		// TODO - NTF handle transaction context
		try {
			getDelegate().send(attachForm);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#send(boolean, java.lang.String)
	 */
	@Override
	public void send(boolean attachForm, String recipient) {
		// TODO - NTF handle transaction context
		try {
			getDelegate().send(attachForm, recipient);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#send(boolean, java.util.Vector)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void send(boolean attachForm, Vector recipients) {
		// TODO - NTF handle transaction context
		try {
			getDelegate().send(attachForm, recipients);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#send(java.lang.String)
	 */
	@Override
	public void send(String recipient) {
		// TODO - NTF handle transaction context
		try {
			getDelegate().send(recipient);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#send(java.util.Vector)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void send(Vector recipients) {
		// TODO - NTF handle transaction context
		try {
			getDelegate().send(recipients);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#setEncryptOnSend(boolean)
	 */
	@Override
	public void setEncryptOnSend(boolean flag) {
		try {
			getDelegate().setEncryptOnSend(flag);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#setEncryptionKeys(java.util.Vector)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setEncryptionKeys(Vector keys) {
		markDirty();
		try {
			getDelegate().setEncryptionKeys(keys);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#setPreferJavaDates(boolean)
	 */
	@Override
	public void setPreferJavaDates(boolean flag) {
		try {
			getDelegate().setPreferJavaDates(flag);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#setSaveMessageOnSend(boolean)
	 */
	@Override
	public void setSaveMessageOnSend(boolean flag) {
		// TODO NTF - mark dirty?
		try {
			getDelegate().setSaveMessageOnSend(flag);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#setSignOnSend(boolean)
	 */
	@Override
	public void setSignOnSend(boolean flag) {
		// TODO NTF - mark dirty?
		try {
			getDelegate().setSignOnSend(flag);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#setUniversalID(java.lang.String)
	 */
	@Override
	public void setUniversalID(String unid) {
		markDirty();
		try {
			getDelegate().setUniversalID(unid);
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#sign()
	 */
	@Override
	public void sign() {
		markDirty();
		try {
			getDelegate().sign();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.Document#unlock()
	 */
	@Override
	public void unlock() {
		try {
			getDelegate().unlock();
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
	}

	void markDirty() {
		isDirty_ = true;
		if (!isQueued_) {
			DatabaseTransaction txn = getParentDatabase().getTransaction();
			if (txn != null) {
				txn.queueUpdate(this);
				isQueued_ = true;
			}
		}
	}

	private boolean queueRemove() {
		if (!isRemoveQueued_) {
			DatabaseTransaction txn = getParentDatabase().getTransaction();
			if (txn != null) {
				txn.queueRemove(this);
				isRemoveQueued_ = true;
				return true; // we queued this, so whoever asked shouldn't do it yet.
			} else {
				return false; // calling function should just go ahead and execute
			}
		} else { // we already queued this for removal.
			return false;
		}
	}

	void clearDirty() {
		isDirty_ = false;
	}

	public void rollback() {
		if (removeType_ != null)
			removeType_ = null;
		if (isDirty()) {
			String nid = getNoteID();
			try {
				lotus.domino.Database delDb = getDelegate().getParentDatabase();
				getDelegate().recycle();
				shouldResurrect_ = true;
				// lotus.domino.Document junkDoc = delDb.createDocument(); // NTF - Why? To make sure I get a new cppid. Otherwise the
				// handle
				// gets reused
				// lotus.domino.Document resetDoc = delDb.getDocumentByID(nid);
				// setDelegate(resetDoc);
				// junkDoc.recycle();
			} catch (NotesException e) {
				DominoUtils.handleException(e);
			}
			clearDirty();
		}
	}

	public boolean isDirty() {
		return isDirty_;
	}

	public boolean forceDelegateRemove() {
		boolean result = false;
		RemoveType type = removeType_;
		try {
			if (type == RemoveType.SOFT_FALSE) {
				return getDelegate().remove(false);
			} else if (type == RemoveType.SOFT_TRUE) {
				return getDelegate().remove(true);
			} else if (type == RemoveType.HARD_TRUE) {
				return getDelegate().removePermanently(true);
			} else if (type == RemoveType.HARD_FALSE) {
				return getDelegate().removePermanently(false);
			}
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return result;
	}

	@Override
	protected lotus.domino.Document getDelegate() {
		lotus.domino.Document d = super.getDelegate();
		try {
			d.isProfile();
		} catch (NotesException recycleSucks) {
			if (shouldResurrect_) {
				resurrect();
			}
		}
		return super.getDelegate();
	}

	private void resurrect() {
		if (noteid_ != null) {
			try {
				lotus.domino.Document d = ((org.openntf.domino.impl.Database) getParentDatabase()).getDelegate().getDocumentByID(noteid_);
				setDelegate(d);
				shouldResurrect_ = false;
				if (log_.isLoggable(Level.FINE)) {
					log_.log(Level.FINE, "Document " + noteid_ + " in database path " + getParentDatabase().getFilePath()
							+ " had been recycled and was auto-restored. Changes may have been lost.");
					if (log_.isLoggable(Level.FINER)) {
						Throwable t = new Throwable();
						StackTraceElement[] elements = t.getStackTrace();
						log_.log(Level.FINER,
								elements[0].getClassName() + "." + elements[0].getMethodName() + " ( line " + elements[0].getLineNumber()
										+ ")");
						log_.log(Level.FINER,
								elements[1].getClassName() + "." + elements[1].getMethodName() + " ( line " + elements[1].getLineNumber()
										+ ")");
						log_.log(Level.FINER,
								elements[2].getClassName() + "." + elements[2].getMethodName() + " ( line " + elements[2].getLineNumber()
										+ ")");
					}
					log_.log(Level.FINE,
							"If you recently rollbacked a transaction and this document was included in the rollback, this outcome is normal.");
				}
			} catch (NotesException e) {
				DominoUtils.handleException(e);
			}
		} else {
			if (log_.isLoggable(Level.WARNING)) {
				log_.log(Level.WARNING,
						"Document doesn't have noteid value. Something went terribly wrong. Nothing good can come of this...");
			}
		}
	}

	/*
	 * Map methods
	 */

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.hasItem(key == null ? null : String.valueOf(key));
	}

	@Override
	public boolean containsValue(Object value) {
		// God, I hope nobody ever actually uses this method
		for (String key : this.keySet()) {
			Object itemVal = this.get(key);
			if ((value == null && itemVal == null) || (value != null && value.equals(itemVal))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		// TODO Implement a "viewing" Set and Map.Entry for this or throw an UnsupportedOperationException
		return null;
	}

	@Override
	public Object get(Object key) {
		if (key == null) {
			return null;
		}
		// Check for "special" cases
		if ("parentDocument".equals(key)) {
			return this.getParentDocument();
		}

		if (this.containsKey(key)) {
			Vector<Object> value = this.getItemValue(key.toString());
			if (value.size() == 1) {
				return value.get(0);
			}
			return value;
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Set<String> keySet() {
		// TODO Implement a "viewing" Set for this or throw an UnsupportedOperationException
		return null;
	}

	@Override
	public Object put(String key, Object value) {
		if (key != null) {
			Object previousState = this.get(key);
			this.removeItem(key);
			this.replaceItemValue(key, value);
			this.save();
			return previousState;
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		for (Map.Entry<? extends String, ? extends Object> entry : m.entrySet()) {
			this.removeItem(entry.getKey());
			this.replaceItemValue(entry.getKey(), entry.getValue());
		}
		this.save();
	}

	@Override
	public Object remove(Object key) {
		if (key != null) {
			Object previousState = this.get(key);
			this.removeItem(key.toString());
			this.save();
			return previousState;
		}
		return null;
	}

	@Override
	public int size() {
		return this.getItems().size();
	}

	@Override
	public Collection<Object> values() {
		// TODO Implement a "viewing" collection for this or throw an UnsupportedOperationException
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.types.DatabaseDescendant#getAncestorDatabase()
	 */
	@Override
	public Database getAncestorDatabase() {
		return this.getParentDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.types.SessionDescendant#getAncestorSession()
	 */
	@Override
	public Session getAncestorSession() {
		return this.getParentDatabase().getParent();
	}
}
