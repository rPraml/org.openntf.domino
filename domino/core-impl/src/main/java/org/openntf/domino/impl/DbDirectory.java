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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.NotesException;

import org.openntf.domino.Database;
import org.openntf.domino.Session;
import org.openntf.domino.WrapperFactory;
import org.openntf.domino.annotations.Legacy;
import org.openntf.domino.ext.Session.Fixes;
import org.openntf.domino.types.Encapsulated;
import org.openntf.domino.utils.ODAUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class DbDirectory.
 */
public class DbDirectory extends BaseNonThreadSafe<org.openntf.domino.DbDirectory, lotus.domino.DbDirectory, Session> implements
		org.openntf.domino.DbDirectory, Encapsulated {
	private static final Logger log_ = Logger.getLogger(DbDirectory.class.getName());

	/* the MetaData contains s small subset of information of a (closed) Database */
	private transient SortedSet<Database.MetaData> dbMetaDataSet_;
	private transient Iterator<Database.MetaData> dbIter; // required for getFirst/getNextDatabase

	private org.openntf.domino.DbDirectory.Type type_;
	private String name_;
	private String clusterName_;
	private boolean isHonorOpenDialog_ = false;

	private Comparator<Database.MetaData> comparator_ = Database.MetaData.FILEPATH_COMPARATOR;

	/**
	 * Instantiates a new DbDirectory.
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
	protected DbDirectory(final lotus.domino.DbDirectory delegate, final Session parent) {
		super(delegate, parent, NOTES_SERVER);
		try {
			name_ = delegate.getName();
			clusterName_ = delegate.getClusterName();
		} catch (NotesException e) {
			ODAUtils.handleException(e);
		}
		//dbHolderSet_ = new ConcurrentSkipListSet<Database.MetaData>(Database.MetaData.FILEPATH_COMPARATOR);
		type_ = Type.TEMPLATE_CANDIDATE;
	}

	@Override
	@Deprecated
	public boolean isSortByLastModified() {
		return comparator_ == Database.MetaData.LASTMOD_COMPARATOR;
	}

	@Override
	@Deprecated
	public void setSortByLastModified(final boolean value) {
		if (value) {
			setComparator(Database.MetaData.LASTMOD_COMPARATOR);
		} else {
			setComparator(Database.MetaData.FILEPATH_COMPARATOR);
		}

	}

	protected void setComparator(final Comparator<Database.MetaData> cmp) {
		if (comparator_ != cmp) {
			comparator_ = cmp;
			reset();
		}
	}

	@Override
	public Type getDirectoryType() {
		return type_;
	}

	@Override
	public void setDirectoryType(final Type type) {
		if (type_ != type) {
			type_ = type;
			reset();
		}
	}

	/**
	 * Convert the lotus int-Type to org.openntf.domino.DbDirectory.Type
	 * 
	 * @param iType
	 * @return
	 */
	protected Type convertType(final int iType) {
		for (Type t : Type.values()) {
			if (t.getValue() == iType)
				return t;
		}
		throw new IllegalArgumentException("The type " + iType + " is not a valid DbDirectory type");
	}

	private void reset() {
		dbMetaDataSet_ = null;
	}

	private void initialize(final lotus.domino.DbDirectory delegate) {
		dbMetaDataSet_ = new ConcurrentSkipListSet<Database.MetaData>(comparator_);
		// boolean isExtended = type_ == Type.REPLICA_CANDIDATE || isDateSorted_;
		boolean isExtended = isSortByLastModified(); // ||  type_ == Type.REPLICA_CANDIDATE; CHECKME: Is there really an issue with REPLICA_CANDIDATE

		try {
			lotus.domino.Database rawdb = null;
			try {
				rawdb = delegate.getFirstDatabase(type_.getValue());
			} catch (NotesException ne) {
				log_.log(Level.WARNING, "For some reason getting the first database reported an exception: " + ne.text
						+ "  Attempting to move along...");
				rawdb = delegate.getNextDatabase();
			}

			while (rawdb != null) {
				boolean wasOpen = rawdb.isOpen(); // remember, if the database was open or not!

				// try to open the DB for extended infos
				if (isExtended) {
					try {
						rawdb.open();
					} catch (NotesException tmp) {
						log_.log(Level.FINE,
								"Unable to read extended infos from database: " + rawdb.getServer() + "!!" + rawdb.getFilePath());
					}
				}

				dbMetaDataSet_.add(new Database.MetaData(rawdb));

				if (wasOpen) {
					getFactory().fromLotus(rawdb, Database.SCHEMA, getAncestorSession());
				} else {
					Base.s_recycle(rawdb);
				}

				rawdb = delegate.getNextDatabase();
			}
			Base.s_recycle(delegate);
		} catch (NotesException ne) {
			ne.printStackTrace();
		}

		//		try {
		//
		//			delegate.setHonorShowInOpenDatabaseDialog(isHonorOpenDialog_);
		//			
		//
		//			// Trying to iterate multiple times over the DbDirectory
		//			for (int i = 0; i < 16; i++) {
		//				int cnt = 0;
		//				rawdb = delegate.getFirstDatabase(1245 + (i % 4));
		//				while (rawdb != null) {
		//					cnt++;
		//					try {
		//						rawdb.open();
		//					} catch (NotesException ne) {
		//					}
		//					rawdb.recycle();
		//					rawdb = delegate.getNextDatabase();
		//				}
		//				System.out.println("iteration " + i + " DBs: " + cnt);
		//			}
		//
		//			try {
		//				rawdb = delegate.getFirstDatabase(type_.getValue());
		//			} catch (NotesException ne) {
		//				log_.log(Level.WARNING, "For some reason getting the first database reported an exception: " + ne.text
		//						+ "  Attempting to move along...");
		//				rawdb = delegate.getNextDatabase();
		//			}
		//			lotus.domino.Database nextdb;
		//			Database db;
		//			boolean accessible = false;
		//			lotus.domino.Database firstDb = rawdb;
		//			while (rawdb != null) {
		//				nextdb = delegate.getNextDatabase();
		//
		//				// RPr: the dbDirectory really sucks...
		//				// Problem 1: Normally the dbDirectory provides "closed" databases
		//				// this means, you get also database objects you do not have access to. If we try to get such a database later by apiPath from the session
		//				// it will fail!
		//				//
		//				// Problem 2: Sometimes you will get the same database objects that were opened somwhere else in your code. This means if you recycle
		//				// the db while you are iterating, this will invalidate the db that was somewhere else opened!
		//
		//				// TODO RPr: Should we do that with factory
		//				// YES, we MUST do this in the factory, otherwise we will get errors like: PANIC! Why are we recaching a lotus object" because there are 
		//				// two openntf.domino instances for the same cpp id
		//				db = getFactory().fromLotus(rawdb, Database.SCHEMA, null);
		//
		//				// And we MUST NOT use this constructor, as it recycles the delegate (which means that we close databases, that we have opened somewhere else in our code)
		//				//db = new org.openntf.domino.impl.Database(rawdb, this, isExtended);
		//
		//				if (type_ == Type.REPLICA_CANDIDATE) {
		//					if (org.openntf.domino.Database.Utils.isReplicaCandidate(db))
		//						dbHolderSet_.add(new Database.MetaData(db));
		//				} else if (type_ == Type.TEMPLATE) {
		//					if (org.openntf.domino.Database.Utils.isTemplate(db))
		//						dbHolderSet_.add(new Database.MetaData(db));
		//				} else if (type_ == Type.DATABASE) {
		//					if (org.openntf.domino.Database.Utils.isDatabase(db))
		//						dbHolderSet_.add(new Database.MetaData(db));
		//				} else if (type_ == Type.XOTS_DATABASE) {
		//					if (org.openntf.domino.Database.Utils.isXotsDatabase(db))
		//						dbHolderSet_.add(new Database.MetaData(db));
		//				} else {
		//					if (org.openntf.domino.Database.Utils.isTemplateCandidate(db))
		//						dbHolderSet_.add(new Database.MetaData(db));
		//				}
		//				// the "db" object will get out of scope here, so that it can get recycled through the GC
		//				rawdb = nextdb;
		//			}
		//			Base.s_recycle(delegate);
		//		} catch (NotesException ne) {
		//			ne.printStackTrace();
		//		}
		//		isInitialized_ = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#createDatabase(java.lang.String)
	 */
	@Override
	public Database createDatabase(final String dbFile) {
		try {
			return fromLotus(getDelegate().createDatabase(dbFile), Database.SCHEMA, getAncestorSession());
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#createDatabase(java.lang.String, boolean)
	 */
	@Override
	public Database createDatabase(final String dbFile, final boolean open) {
		org.openntf.domino.Database result = null;
		try {
			if (getAncestorSession().isFixEnabled(Fixes.CREATE_DB)) {
				result = openDatabase(dbFile, false);
				if (result != null)
					return result;
			}
			lotus.domino.Database delDb = getDelegate().createDatabase(dbFile, open);
			result = fromLotus(delDb, Database.SCHEMA, getAncestorSession());
			return result;
		} catch (NotesException e) {
			//			System.out.println("DEBUG: " + e.id + " - " + e.text);
			if (e.id == 4005 && getAncestorSession().isFixEnabled(Fixes.CREATE_DB)) {
				result = getAncestorSession().getDatabase(getName(), dbFile);
				return result;
			} else {
				ODAUtils.handleException(e);
				return null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#getClusterName()
	 */
	@Override
	public String getClusterName() {
		return clusterName_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#getClusterName(java.lang.String)
	 */
	@Override
	public String getClusterName(final String server) {
		try {
			return getDelegate().getClusterName(server);
		} catch (NotesException e) {
			ODAUtils.handleException(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#getFirstDatabase(int)
	 */
	@Override
	@Deprecated
	@Legacy(org.openntf.domino.annotations.Legacy.ITERATION_WARNING)
	public Database getFirstDatabase(final int type) {
		setDirectoryType(convertType(type));
		return getFirstDatabase();
	}

	/* (non-Javadoc)
	 * @see org.openntf.domino.DbDirectory#getFirstDatabase(org.openntf.domino.DbDirectory.Type)
	 */
	@Override
	@Deprecated
	@Legacy(org.openntf.domino.annotations.Legacy.ITERATION_WARNING)
	public Database getFirstDatabase(final Type type) {
		setDirectoryType(type);
		return getFirstDatabase();
	}

	@Deprecated
	@Legacy(org.openntf.domino.annotations.Legacy.ITERATION_WARNING)
	public Database getFirstDatabase() {
		dbIter = getMetaDataSet().iterator();
		return getNextDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#getName()
	 */
	@Override
	public String getName() {
		return name_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#getNextDatabase()
	 */
	@Override
	@Deprecated
	@Legacy(org.openntf.domino.annotations.Legacy.ITERATION_WARNING)
	public Database getNextDatabase() {
		// RPr: hopefully this will work the same way as the original lotus implementation does
		if (dbIter.hasNext()) {
			return getFactory().create(Database.SCHEMA, getAncestorSession(), dbIter.next());
		}
		return null;
		// This will never work, as the DBs in the getDbHolderSet() are in a complete different order 
		//		try {
		//			return fromLotus(getDelegate().getNextDatabase(), Database.SCHEMA, getAncestorSession());
		//		} catch (NotesException e) {
		//			ODAUtils.handleException(e);
		//			return null;
		//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.impl.Base#getParent()
	 */
	@Override
	public final Session getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#isHonorShowInOpenDatabaseDialog()
	 */
	@Override
	public boolean isHonorShowInOpenDatabaseDialog() {
		return isHonorOpenDialog_;
	}

	@Override
	public SortedSet<Database.MetaData> getMetaDataSet() {
		//		if (!isInitialized_) {
		//			initialize(getDelegate());
		//		}
		//		return dbHolderSet_;
		if (dbMetaDataSet_ == null) {
			initialize(getDelegate());
		}
		return dbMetaDataSet_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<org.openntf.domino.Database> iterator() {
		// Create a proxy iterator
		return new Iterator<org.openntf.domino.Database>() {
			final Iterator<Database.MetaData> metaIter_ = getMetaDataSet().iterator();

			@Override
			public boolean hasNext() {
				return metaIter_.hasNext();
			}

			@Override
			public Database next() {
				return getFactory().create(Database.SCHEMA, getAncestorSession(), metaIter_.next());
			}

			@Override
			public void remove() {
				metaIter_.remove();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#openDatabase(java.lang.String)
	 */
	@Override
	public Database openDatabase(final String dbFile) {
		return getAncestorSession().getDatabase(getName(), dbFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#openDatabase(java.lang.String, boolean)
	 */
	@Override
	public Database openDatabase(final String dbFile, final boolean failover) {
		if (failover) {
			return getAncestorSession().getDatabaseWithFailover(getName(), dbFile);
		} else {
			return openDatabase(dbFile);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#openDatabaseByReplicaID(java.lang.String)
	 */
	@Override
	public Database openDatabaseByReplicaID(final String replicaId) {
		return getAncestorSession().getDatabase(getName(), replicaId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#openDatabaseIfModified(java.lang.String, lotus.domino.DateTime)
	 */
	@Override
	public Database openDatabaseIfModified(final String dbFile, final lotus.domino.DateTime date) {
		return getAncestorSession().getDatabaseIfModified(getName(), dbFile, date);
	}

	@Override
	public Database openDatabaseIfModified(final String dbFile, final Date date) {
		return getAncestorSession().getDatabaseIfModified(getName(), dbFile, date);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#openMailDatabase()
	 */
	@Override
	public Database openMailDatabase() {
		return getAncestorSession().getMailDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DbDirectory#setHonorShowInOpenDatabaseDialog(boolean)
	 */
	@Override
	public void setHonorShowInOpenDatabaseDialog(final boolean flag) {
		if (isHonorOpenDialog_ != flag) {
			isHonorOpenDialog_ = flag;
			reset();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.types.SessionDescendant#getAncestorSession()
	 */
	@Override
	public final Session getAncestorSession() {
		return parent;
	}

	@Override
	protected void resurrect() {
		lotus.domino.Session rawSession = toLotus(parent);
		try {
			lotus.domino.DbDirectory dir = rawSession.getDbDirectory(name_);
			dir.setHonorShowInOpenDatabaseDialog(isHonorOpenDialog_);
			if (log_.isLoggable(java.util.logging.Level.FINE)) {
				Throwable t = new Throwable();
				log_.log(java.util.logging.Level.FINE, "DbDirectory for server " + name_ + "had been recycled and was auto-restored.", t);
			}

			setDelegate(dir, true);
		} catch (Exception e) {
			ODAUtils.handleException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		comparator_ = (Comparator<Database.MetaData>) in.readObject();
		isHonorOpenDialog_ = in.readBoolean();
		type_ = Type.valueOf(in.readInt());
		name_ = in.readUTF();
		clusterName_ = in.readUTF();
		// CHECKME should we really 
		dbMetaDataSet_ = (SortedSet<Database.MetaData>) in.readObject();
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeObject(comparator_);
		out.writeBoolean(isHonorOpenDialog_);
		out.writeInt(type_.getValue());
		out.writeUTF(name_);
		out.writeUTF(clusterName_);
		out.writeObject(dbMetaDataSet_);
	}

	@Override
	public boolean add(final Database db) {
		try {
			return getMetaDataSet().add(new Database.MetaData(db));
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
			return false;
		}
	}

	@Override
	public boolean addAll(final Collection<? extends Database> dbs) {
		boolean ret = false;
		for (Database db : dbs) {
			if (add(db)) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void clear() {
		if (dbMetaDataSet_ == null) {
			dbMetaDataSet_ = new ConcurrentSkipListSet<Database.MetaData>(comparator_);
		} else {
			dbMetaDataSet_.clear();
		}
	}

	@Override
	public boolean contains(final Object obj) {
		if (obj instanceof Database) {
			try {
				return getMetaDataSet().contains(new Database.MetaData((Database) obj));
			} catch (NotesException ne) {
				ODAUtils.handleException(ne);
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean containsAll(final Collection<?> objs) {
		for (Object obj : objs) {
			if (!contains(obj)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return getMetaDataSet().isEmpty();
	}

	@Override
	public boolean remove(final Object obj) {
		if (obj instanceof Database) {
			try {
				return getMetaDataSet().remove(new Database.MetaData((Database) obj));
			} catch (NotesException ne) {
				ODAUtils.handleException(ne);
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean removeAll(final Collection<?> objs) {
		boolean ret = false;
		for (Object obj : objs) {
			if (remove(obj)) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public boolean retainAll(final Collection<?> objs) {
		Collection<Database.MetaData> holders = new ArrayList<Database.MetaData>(objs.size());

		for (Object obj : objs) {
			if (obj instanceof Database) {
				try {
					holders.add(new Database.MetaData((Database) obj));
				} catch (NotesException ne) {
					ODAUtils.handleException(ne);
					return false;
				}
			}
		}
		return getMetaDataSet().retainAll(holders);
	}

	@Override
	public int size() {
		return getMetaDataSet().size();
	}

	@Override
	public Object[] toArray() {
		Object[] ret = new Object[size()];
		int i = 0;
		for (Database.MetaData metaData : getMetaDataSet()) {
			Database db = getFactory().create(Database.SCHEMA, getAncestorSession(), metaData);
			ret[i++] = db;
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] paramArrayOfT) {
		// DUH!
		if (size() > paramArrayOfT.length) {
			Class<?> localClass = paramArrayOfT.getClass().getComponentType();
			paramArrayOfT = (T[]) Array.newInstance(localClass, size());
		}
		System.arraycopy(toArray(), 0, paramArrayOfT, 0, size());
		if (size() < paramArrayOfT.length) {
			paramArrayOfT[size()] = null;
		}
		return paramArrayOfT;
	}

	@Override
	protected WrapperFactory getFactory() {
		return parent.getFactory();
	}

}
