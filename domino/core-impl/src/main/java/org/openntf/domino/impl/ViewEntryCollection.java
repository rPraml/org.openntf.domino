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
package org.openntf.domino.impl;

import java.util.Iterator;
import java.util.Map;

import lotus.domino.NotesException;

import org.openntf.domino.Database;
import org.openntf.domino.Session;
import org.openntf.domino.View;
import org.openntf.domino.ViewEntry;
import org.openntf.domino.WrapperFactory;
import org.openntf.domino.utils.ODAUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ViewEntryCollection.
 */
public class ViewEntryCollection extends BaseNonThreadSafe<org.openntf.domino.ViewEntryCollection, lotus.domino.ViewEntryCollection, View>
		implements org.openntf.domino.ViewEntryCollection {

	/**
	 * Instantiates a new outline.
	 * 
	 * @param delegate
	 *            the delegate
	 * @param parent
	 *            the parent
	 * @param wf
	 *            the wrapperfactory
	 * @param cppId
	 *            the cpp-id
	 */
	protected ViewEntryCollection(final lotus.domino.ViewEntryCollection delegate, final View parent) {
		super(delegate, parent, NOTES_VECOLL);
	}

	// FIXME NTF -- all method that return a ViewEntry probably need to parent to the View rather than the Collection. Someone should verify
	// the defined behavior.
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#addEntry(java.lang.Object)
	 */
	@Override
	public void addEntry(final Object obj) {
		try {
			getDelegate().addEntry(toLotus(obj));
		} catch (NotesException e) {
			ODAUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#addEntry(java.lang.Object, boolean)
	 */
	@Override
	public void addEntry(final Object obj, final boolean checkDups) {
		try {
			getDelegate().addEntry(toLotus(obj), checkDups);
		} catch (NotesException e) {
			ODAUtils.handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#cloneCollection()
	 */
	@Override
	public org.openntf.domino.ViewEntryCollection cloneCollection() {
		try {
			return fromLotus(getDelegate().cloneCollection(), ViewEntryCollection.SCHEMA, parent);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<org.openntf.domino.ViewEntry> iterator() {
		return new Iterator<org.openntf.domino.ViewEntry>() {

			private org.openntf.domino.ViewEntry currEntry = null;
			private org.openntf.domino.ViewEntry nextEntry = ViewEntryCollection.this.getFirstEntry();

			@Override
			public boolean hasNext() {
				return nextEntry != null; // something in Queue?
			}

			/* (non-Javadoc)
			 * @see java.util.Iterator#next()
			 */
			@SuppressWarnings("deprecation")
			@Override
			public org.openntf.domino.ViewEntry next() {

				if (nextEntry == null || nextEntry.isDead()) {
					if (nextEntry == null) {
						System.out.println("ALERT: Wrapped version of next entry is NULL");
					} else if (nextEntry.isDead()) {
						System.out.println("ALERT: Wrapped version of next entry is dead");
					} else {
						System.out.println("ALERT: It should have been impossible to arrive here");
						throw new RuntimeException();
					}
				}
				currEntry = nextEntry;
				if (nextEntry != null) {
					nextEntry = ViewEntryCollection.this.getNextEntry(nextEntry);
				}
				return currEntry;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#contains(lotus.domino.Base)
	 */
	@Override
	public boolean contains(final lotus.domino.Base entries) {
		try {
			return getDelegate().contains(toLotus(entries));
		} catch (Throwable t) {
			ODAUtils.handleException(t);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#contains(int)
	 */
	@Override
	public boolean contains(final int noteId) {
		try {
			boolean result = getDelegate().contains(noteId);
			return result;
		} catch (Throwable t) {
			ODAUtils.handleException(t);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#contains(java.lang.String)
	 */
	@Override
	public boolean contains(final String noteId) {
		try {
			boolean result = getDelegate().contains(noteId);
			return result;
		} catch (Throwable t) {
			ODAUtils.handleException(t);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#deleteEntry(lotus.domino.ViewEntry)
	 */
	@Override
	public void deleteEntry(final lotus.domino.ViewEntry entry) {
		try {
			getDelegate().deleteEntry(toLotus(entry));
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#FTSearch(java.lang.String)
	 */
	@Override
	public void FTSearch(final String query) {
		try {
			getDelegate().FTSearch(query);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#FTSearch(java.lang.String, int)
	 */
	@Override
	public void FTSearch(final String query, final int maxDocs) {
		try {
			getDelegate().FTSearch(query, maxDocs);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#getCount()
	 */
	@Override
	public int getCount() {
		try {
			return getDelegate().getCount();
		} catch (Throwable t) {
			ODAUtils.handleException(t);
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#getEntry(java.lang.Object)
	 */
	@Override
	public ViewEntry getEntry(final Object entry) {
		try {
			return fromLotus(getDelegate().getEntry(toLotus(entry)), ViewEntry.SCHEMA, parent);
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#getFirstEntry()
	 */
	@Override
	public ViewEntry getFirstEntry() {
		try {
			return fromLotus(getDelegate().getFirstEntry(), ViewEntry.SCHEMA, parent);
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#getLastEntry()
	 */
	@Override
	public ViewEntry getLastEntry() {
		try {
			return fromLotus(getDelegate().getLastEntry(), ViewEntry.SCHEMA, parent);
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#getNextEntry()
	 */
	@Override
	public ViewEntry getNextEntry() {
		try {
			return fromLotus(getDelegate().getNextEntry(), ViewEntry.SCHEMA, parent);
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#getNextEntry(lotus.domino.ViewEntry)
	 */
	@Override
	public ViewEntry getNextEntry(final lotus.domino.ViewEntry entry) {
		try {
			ViewEntry result = fromLotus(getDelegate().getNextEntry(toLotus(entry)), ViewEntry.SCHEMA, parent);
			entry.recycle();
			return result;
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#getNthEntry(int)
	 */
	@Override
	@Deprecated
	public ViewEntry getNthEntry(final int n) {
		return getEntryAt(n);
	}

	@Override
	public ViewEntry getEntryAt(final int n) {
		try {
			return fromLotus(getDelegate().getNthEntry(n), ViewEntry.SCHEMA, parent);
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.impl.Base#getParent()
	 */
	@Override
	public final View getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#getPrevEntry()
	 */
	@Override
	public ViewEntry getPrevEntry() {
		try {
			return fromLotus(getDelegate().getPrevEntry(), ViewEntry.SCHEMA, parent);
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#getPrevEntry(lotus.domino.ViewEntry)
	 */
	@Override
	public ViewEntry getPrevEntry(final lotus.domino.ViewEntry entry) {
		try {
			ViewEntry result = fromLotus(getDelegate().getPrevEntry(toLotus(entry)), ViewEntry.SCHEMA, parent);
			entry.recycle();
			return result;
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#getQuery()
	 */
	@Override
	public String getQuery() {
		try {
			return getDelegate().getQuery();
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#intersect(lotus.domino.Base)
	 */
	@Override
	public void intersect(final lotus.domino.Base entries) {
		try {
			getDelegate().intersect(toLotus(entries));
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#intersect(int)
	 */
	@Override
	public void intersect(final int noteId) {
		try {
			getDelegate().intersect(noteId);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#intersect(java.lang.String)
	 */
	@Override
	public void intersect(final String noteId) {
		try {
			getDelegate().intersect(noteId);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#markAllRead()
	 */
	@Override
	public void markAllRead() {
		try {
			getDelegate().markAllRead();
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#markAllRead(java.lang.String)
	 */
	@Override
	public void markAllRead(final String userName) {
		try {
			getDelegate().markAllUnread(userName);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#markAllUnread()
	 */
	@Override
	public void markAllUnread() {
		try {
			getDelegate().markAllUnread();
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#markAllUnread(java.lang.String)
	 */
	@Override
	public void markAllUnread(final String userName) {
		try {
			getDelegate().markAllUnread(userName);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#merge(lotus.domino.Base)
	 */
	@Override
	public void merge(final lotus.domino.Base entries) {
		try {
			getDelegate().merge(toLotus(entries));
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#merge(int)
	 */
	@Override
	public void merge(final int noteId) {
		try {
			getDelegate().merge(noteId);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#merge(java.lang.String)
	 */
	@Override
	public void merge(final String noteId) {
		try {
			getDelegate().merge(noteId);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#putAllInFolder(java.lang.String)
	 */
	@Override
	public void putAllInFolder(final String folderName) {
		try {
			getDelegate().putAllInFolder(folderName);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#putAllInFolder(java.lang.String, boolean)
	 */
	@Override
	public void putAllInFolder(final String folderName, final boolean createOnFail) {
		try {
			getDelegate().putAllInFolder(folderName, createOnFail);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#removeAll(boolean)
	 */
	@Override
	public void removeAll(final boolean force) {
		try {
			getDelegate().removeAll(force);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#removeAllFromFolder(java.lang.String)
	 */
	@Override
	public void removeAllFromFolder(final String folderName) {
		try {
			getDelegate().removeAllFromFolder(folderName);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#stampAll(java.lang.String, java.lang.Object)
	 */
	@Override
	public void stampAll(final String itemName, final Object value) {
		try {
			getDelegate().stampAll(itemName, value);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	@Override
	public void stampAll(final Map<String, Object> map) {
		for (org.openntf.domino.ViewEntry entry : this) {
			entry.getDocument().putAll(map);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#subtract(lotus.domino.Base)
	 */
	@Override
	public void subtract(final lotus.domino.Base entries) {
		try {
			getDelegate().subtract(toLotus(entries));
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#subtract(int)
	 */
	@Override
	public void subtract(final int noteId) {
		try {
			getDelegate().subtract(noteId);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#subtract(java.lang.String)
	 */
	@Override
	public void subtract(final String noteId) {
		try {
			getDelegate().subtract(noteId);
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ViewEntryCollection#updateAll()
	 */
	@Override
	public void updateAll() {
		try {
			getDelegate().updateAll();
		} catch (Throwable t) {
			ODAUtils.handleException(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.types.DatabaseDescendant#getAncestorDatabase()
	 */
	@Override
	public final Database getAncestorDatabase() {
		return parent.getAncestorDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.types.SessionDescendant#getAncestorSession()
	 */
	@Override
	public final Session getAncestorSession() {
		return this.getAncestorDatabase().getAncestorSession();
	}

	@Override
	protected WrapperFactory getFactory() {
		return parent.getAncestorSession().getFactory();
	}

}
