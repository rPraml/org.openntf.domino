/*
 * Copyright 2013
 * 
 * @author Devin S. Olson (dolson@czarnowski.com)
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
 *
 */
package org.openntf.domino.utils;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import lotus.domino.NotesException;

import org.openntf.domino.Document;
import org.openntf.domino.commons.Strings;
import org.openntf.domino.iterators.DocumentList;

/**
 * CollectionUtils (Sets and Lists) utilities library
 * 
 * @author Devin S. Olson (dolson@czarnowski.com)
 * 
 *         Roland Praml: org.openntf.domino.commons.utils.CollectionUtils for deprecated stuff (There are only two methods left)
 * 
 */
public enum CollectionUtils {
	;

	@Deprecated
	public static class ChainedIterable<T> extends org.openntf.domino.commons.utils.CollectionUtils.ChainedIterable<T> {
	};

	/**
	 * Gets or generates an List of Strings from an Item on a Document
	 * 
	 * @param source
	 *            Document from which to get or generate the result.
	 * 
	 * @param itemname
	 *            Name of item from which to get the content
	 * 
	 * @return List of Strings retrieved or generated from the input. Returns null if the document does not contain the item.
	 * 
	 * @throws IllegalArgumentException
	 *             if source document is null or itemname is blank or null.
	 */
	public static List<String> getListStrings(final Document source, final String itemname) {
		if (null == source) {
			throw new IllegalArgumentException("Source document is null");
		}
		if (Strings.isBlankString(itemname)) {
			throw new IllegalArgumentException("ItemName is blank or null");
		}

		return (source.hasItem(itemname)) ? CollectionUtils.getListStrings(source.getItemValue(itemname)) : null;
	}

	@Deprecated
	public static List<String> getListStrings(final Vector vector) {
		return org.openntf.domino.commons.utils.CollectionUtils.getListStrings(vector);
	}

	@Deprecated
	@SuppressWarnings("rawtypes")
	public static List<String> getListStrings(final AbstractCollection collection) {
		return org.openntf.domino.commons.utils.CollectionUtils.getListStrings(collection);
	}

	@Deprecated
	@SuppressWarnings("rawtypes")
	public static List<String> getListStrings(final AbstractMap map) {
		return org.openntf.domino.commons.utils.CollectionUtils.getListStrings(map);
	}

	@Deprecated
	public static List<String> getListStrings(final String string) {
		return org.openntf.domino.commons.utils.CollectionUtils.getListStrings(string);
	}

	@Deprecated
	public static List<String> getListStrings(final String[] strings) {
		return org.openntf.domino.commons.utils.CollectionUtils.getListStrings(strings);
	}

	@Deprecated
	@SuppressWarnings("rawtypes")
	public static List<String> getListStrings(final Object object) {
		return org.openntf.domino.commons.utils.CollectionUtils.getListStrings(object);
	}

	@Deprecated
	public static TreeSet<String> getTreeSetStrings(final Object object) {
		return org.openntf.domino.commons.utils.CollectionUtils.getTreeSetStrings(object);
	}

	@Deprecated
	public static String[] getStringArray(final Object object) {
		return org.openntf.domino.commons.utils.CollectionUtils.getStringArray(object);
	}

	@Deprecated
	public static String[] getSortedUnique(final Object object) {
		return org.openntf.domino.commons.utils.CollectionUtils.getSortedUnique(object);
	}

	@Deprecated
	public static int compareStringArrays(final String[] stringarray0, final String[] stringarray1, final boolean descending) {
		return org.openntf.domino.commons.utils.CollectionUtils.compareStringArrays(stringarray0, stringarray1, descending);
	}

	@Deprecated
	public static int compareTreeSetStrings(final TreeSet<String> treeset0, final TreeSet<String> treeset1, final boolean descending) {
		return org.openntf.domino.commons.utils.CollectionUtils.compareTreeSetStrings(treeset0, treeset1, descending);
	}

	@Deprecated
	public static TreeSet<String> getTreeSetStringsBeginsWith(final Object source, final String prefix) {
		return org.openntf.domino.commons.utils.CollectionUtils.getTreeSetStringsBeginsWith(source, prefix);
	}

	@Deprecated
	public static <T> Iterable<T> chain(final Iterable<T>... iterables) {
		return org.openntf.domino.commons.utils.CollectionUtils.chain(iterables);
	}

	//	/**
	//	 * Convert a Document collection to Notes Collection
	//	 * 
	//	 * @param collection
	//	 * @return
	//	 */
	//	public static org.openntf.domino.NoteCollection toLotusNoteCollection(final lotus.domino.DocumentCollection collection) {
	//		org.openntf.domino.NoteCollection result = null;
	//		if (collection instanceof org.openntf.domino.DocumentCollection) {
	//			org.openntf.domino.Database db = ((org.openntf.domino.DocumentCollection) collection).getAncestorDatabase();
	//			result = db.createNoteCollection(false);
	//			result.add(collection);
	//		} else if (collection != null) {
	//			// TODO Eh?
	//			org.openntf.domino.Database db = ((org.openntf.domino.DocumentCollection) collection).getAncestorDatabase();
	//			result = db.createNoteCollection(false);
	//			result.add(collection);
	//		}
	//		return result;
	//	}

	/**
	 * Returns the Note IDs of the given (Notes) collection
	 * 
	 * @param collection
	 *            the DocumentCollection
	 * @return a array of NoteIDs
	 */
	public static int[] getNoteIDs(final lotus.domino.DocumentCollection collection) {
		int[] result = null;
		try {
			if (collection instanceof DocumentList) {
				result = ((DocumentList) collection).getNids();
			} else if (collection.isSorted()) {
				if (collection instanceof org.openntf.domino.DocumentCollection) {
					org.openntf.domino.DocumentCollection ocoll = (org.openntf.domino.DocumentCollection) collection;
					int size = ocoll.getCount();
					result = new int[size];
					int i = 0;
					for (org.openntf.domino.Document doc : ocoll) {
						result[i++] = Integer.valueOf(doc.getNoteID(), 16);
					}
				} else {
					int size = collection.getCount();
					result = new int[size];
					lotus.domino.Document doc = collection.getFirstDocument();
					lotus.domino.Document next = null;
					int i = 0;
					while (doc != null) {
						next = collection.getNextDocument(doc);
						result[i++] = Integer.valueOf(doc.getNoteID(), 16);
						doc.recycle();
						doc = next;
					}
				}
			} else {
				lotus.domino.Database db = collection.getParent();
				lotus.domino.NoteCollection nc = db.createNoteCollection(false);
				result = nc.getNoteIDs();
				nc.recycle();
			}
		} catch (NotesException e) {
			DominoUtils.handleException(e);
		}
		return result;
	}
}
