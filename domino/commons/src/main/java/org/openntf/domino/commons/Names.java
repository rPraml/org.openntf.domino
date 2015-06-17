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
package org.openntf.domino.commons;

import java.util.ArrayList;
import java.util.List;

import org.openntf.domino.commons.NameEnums.NamePartKey;
import org.openntf.domino.commons.impl.NameImpl;
import org.openntf.domino.commons.utils.CollectionUtils;

/**
 * Name handling utilities
 * 
 * @author Devin S. Olson (dolson@czarnowski.com), Roland Praml
 */
public enum Names {
	;

	/**
	 * Creates a new INameParser object based on the given name
	 */
	public static IName parse(final String name) {
		return new NameImpl(name);
	}

	/**
	 * Sets the local server name (i.e. default name)
	 */
	public static void setLocalServerName(final String localServerName) {
		NameImpl.setLocalServerName(localServerName);
	}

	/**
	 * Formats a String as a role (begins with "[", ends with "]")
	 * 
	 * @param string
	 *            String to be formatted
	 * 
	 * @return source string formatted as a role.
	 */
	public static String formatAsRole(final String string) {
		if (!Strings.isBlankString(string)) {
			final String result = "[" + string.replace("[", "").replace("]", "").trim() + "]";
			return ("[]".equals(result)) ? "" : result;
		}

		return "";
	}

	/**
	 * Gets the approprite formatted name string for the given source.
	 * 
	 * @param source
	 *            Source string from which to generate the name
	 * 
	 * @param key
	 *            Part of name string to return
	 * 
	 * @return source string converted to the appropriate name format
	 */
	public static String getNamePart(final String source, final NamePartKey key) {
		if (null == key) {
			throw new IllegalArgumentException("NamePartKey is null");
		}
		return parse(source).getNamePart(key);
	}

	/**
	 * Gets the approprite formatted name strings for the given source. The order of values is preserved, however duplicates are removed and
	 * no values will be included for null or blank entries in the source.
	 * 
	 * @param session
	 *            Session to use
	 * 
	 * @param source
	 *            Source strings from which to generate the names.
	 * 
	 * @param key
	 *            Part of name string to return
	 * 
	 * @return source strings converted to the appropriate name format, in the order in which they exist in source.
	 */
	public static String[] getNameParts(final String[] source, final NamePartKey key) {

		if (null == source) {
			throw new IllegalArgumentException("Source Array is null");
		}

		if (null == key) {
			throw new IllegalArgumentException("NamePartsMap.Key is null");
		}

		if (source.length < 1) {
			return null;
		}

		final List<String> values = new ArrayList<String>();
		for (final String temp : source) {
			if (!Strings.isBlankString(temp)) {
				final String name = getNamePart(temp, key);

				if (!Strings.isBlankString(name)) {
					values.add(name);
				}
			}
		}

		return (values.size() < 1) ? null : CollectionUtils.getStringArray(values);
	}

	/**
	 * Generates an array of Strings containing Abbreviated names.
	 * 
	 * @param source
	 *            Source strings from which to generate the names.
	 * 
	 * @return source strings converted to the appropriate name format, in the order in which they exist in source.
	 */
	public static String[] getAbbreviated(final String[] source) {
		return getNameParts(source, NamePartKey.Abbreviated);
	}

	/**
	 * Generates an Abbreviated Name from the input.
	 * 
	 * @param source
	 *            String from which to generate the names.
	 * 
	 * @return source String converted to the appropriate name format
	 */
	public static String getAbbreviated(final String source) {
		return getNamePart(source, NamePartKey.Abbreviated);
	}

	/**
	 * Generates an array of Strings containing Canonical names.
	 * 
	 * @param source
	 *            Source strings from which to generate the names.
	 * 
	 * @return source strings converted to the appropriate name format, in the order in which they exist in source.
	 */
	public static String[] getCanonical(final String[] source) {
		return getNameParts(source, NamePartKey.Canonical);
	}

	/**
	 * Generates an Canonical Name from the input.
	 * 
	 * @param source
	 *            String from which to generate the names.
	 * 
	 * @return source String converted to the appropriate name format
	 */
	public static String getCanonical(final String source) {
		return getNamePart(source, NamePartKey.Canonical);
	}

	/**
	 * Generates an array of Strings containing Common names.
	 * 
	 * @param session
	 *            Session to use
	 * 
	 * @param source
	 *            Source strings from which to generate the names.
	 * 
	 * @return source strings converted to the appropriate name format, in the order in which they exist in source.
	 */
	public static String[] getCommon(final String[] source) {
		return getNameParts(source, NamePartKey.Common);
	}

	/**
	 * Generates an Common Name from the input.
	 * 
	 * @param source
	 *            String from which to generate the names.
	 * 
	 * @return source String converted to the appropriate name format
	 */
	public static String getCommon(final String source) {
		return getNamePart(source, NamePartKey.Common);
	}

}
