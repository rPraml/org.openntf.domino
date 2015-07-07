/*
 * Copyright 2014
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
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.logging.Logger;

import lotus.domino.NotesException;
import lotus.notes.addins.DominoServer;

import org.openntf.domino.Session;
import org.openntf.domino.WrapperFactory;
import org.openntf.domino.commons.IName;
import org.openntf.domino.commons.NameEnums.NameError;
import org.openntf.domino.commons.NameEnums.NameFormat;
import org.openntf.domino.commons.NameEnums.NamePartKey;
import org.openntf.domino.commons.Strings;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.utils.ODAUtils;

/**
 * The class NameODA - alternative implementation for Name
 * 
 * Name should not be used, use IName instead
 * 
 * @author Praml, Steinsiek
 * 
 */
public class NameODA extends BaseNonThreadSafe<org.openntf.domino.Name, lotus.domino.Name, Session> implements org.openntf.domino.Name,
		Comparable<org.openntf.domino.commons.IName>, Cloneable {
	@SuppressWarnings("unused")
	private static final Logger log_ = Logger.getLogger(NameODA.class.getName());

	/*-------------------------------------------------------------------------------------*/
	/*
	 * Constructors
	 */
	private String _language;
	private IName _parserDelegate;

	@Deprecated
	// Needed for Externalization
	public NameODA() {
		super(null, Factory.getSession(SessionType.CURRENT), NOTES_NAME);
	}

	// Called from WrapperFactory.create
	protected NameODA(final Session sess, final String name, final String lang) {
		super(null, sess, NOTES_NAME);
		_language = Strings.null2Empty(lang);
		_parserDelegate = IName.$.create(name);
	}

	// Called from WrapperFactory.wrapLotusObject
	protected NameODA(final lotus.domino.Name delegate, final Session parent) {
		super(delegate, parent, NOTES_NAME);
		try {
			_language = delegate.getLanguage();
			_parserDelegate = IName.$.create(delegate.getCanonical());
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		} finally {
			// WARNING: Wrapping recycles the caller's object. This may cause issues, if 
			// the Lotus object is used outside openNTF
			Base.s_recycle(delegate);
		}
	}

	/*-------------------------------------------------------------------------------------*/
	/*
	 * ODA methods
	 */
	@Override
	protected lotus.domino.Name getDelegate() {
		try {
			lotus.domino.Session rawsession = toLotus(parent);
			return rawsession.createName(this.getCanonical());
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		}
		return null;
	}

	@Override
	protected WrapperFactory getFactory() {
		return parent.getFactory();
	}

	@Override
	public final Session getAncestorSession() {
		return parent;
	}

	@Override
	public final Session getParent() {
		return parent;
	}

	/*-------------------------------------------------------------------------------------*/
	/*
	 * A few additional methods
	 */
	@Override
	// Since Name is immutable (at moment), this simply becomes:
	public NameODA clone() {
		return this;
	}

	@Override
	public boolean isHierarchical() {
		return _parserDelegate.isHierarchical();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<String> getGroups(final String serverName) {
		Collection<String> result = null;
		try {
			DominoServer server = new DominoServer(serverName);
			result = server.getNamesList(getCanonical());
		} catch (NotesException e) {
			ODAUtils.handleException(e);
		}
		return result;
	}

	@Override
	public NameFormat getNameFormat() {
		return _parserDelegate.getNameFormat();
	}

	@Override
	public NameError getNameError() {
		return _parserDelegate.getNameError();
	}

	/*-------------------------------------------------------------------------------------*/
	/*
	 * toString, equals etc.
	 */
	@Override
	public String toString() {
		return getClass().getName() + " [Canonical='" + getCanonical() + "', Language='" + _language + "']";
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof NameODA))	// This comprises obj = null
			return false;
		NameODA other = (NameODA) obj;
		return _parserDelegate.equals(other._parserDelegate) && _language.equals(other._language);
	}

	@Override
	public int compareTo(final org.openntf.domino.commons.IName other) {
		if (other == null)
			return 1;
		return getCanonical().compareTo(other.getCanonical());
	}

	/*-------------------------------------------------------------------------------------*/
	/*
	 * Externalization
	 */
	private static final int EXTERNALVERSIONUID = 20141219; // The current date (when it was implemented)

	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		int version = in.readInt();
		if (version != EXTERNALVERSIONUID)
			throw new InvalidClassException("Cannot read data version " + version);
		String canonical = in.readUTF();
		_language = in.readUTF();
		_parserDelegate = IName.$.create(canonical);
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(EXTERNALVERSIONUID);
		out.writeUTF(getCanonical());
		out.writeUTF(_language);
	}

	/*-------------------------------------------------------------------------------------*/
	/*
	 * Lotus-get-methods
	 */
	@Override
	public String getAbbreviated() {
		return _parserDelegate.getAbbreviated();
	}

	@Override
	public String getAddr821() {
		return _parserDelegate.getAddr821();
	}

	@Override
	public String getAddr822Comment1() {
		return _parserDelegate.getAddr822Comment1();
	}

	@Override
	public String getAddr822Comment2() {
		return _parserDelegate.getAddr822Comment2();
	}

	@Override
	public String getAddr822Comment3() {
		return _parserDelegate.getAddr822Comment3();
	}

	@Override
	public String getAddr822LocalPart() {
		return _parserDelegate.getAddr822LocalPart();
	}

	@Override
	public String getAddr822Phrase() {
		return _parserDelegate.getAddr822Phrase();
	}

	@Override
	public String getADMD() {
		return _parserDelegate.getADMD();
	}

	@Override
	public String getCanonical() {
		return _parserDelegate.getCanonical();
	}

	@Override
	public String getCommon() {
		return _parserDelegate.getCommon();
	}

	@Override
	public String getCountry() {
		return _parserDelegate.getCountry();
	}

	@Override
	public String getGeneration() {
		return _parserDelegate.getGeneration();
	}

	@Override
	public String getGiven() {
		return _parserDelegate.getGiven();
	}

	@Override
	public String getInitials() {
		return _parserDelegate.getInitials();
	}

	@Override
	public String getKeyword() {
		return _parserDelegate.getKeyword();
	}

	@Override
	public String getLanguage() {
		return _language;
	}

	@Override
	public String getOrganization() {
		return _parserDelegate.getOrganization();
	}

	@Override
	public String getOrgUnit1() {
		return _parserDelegate.getOrgUnit1();
	}

	@Override
	public String getOrgUnit2() {
		return _parserDelegate.getOrgUnit2();
	}

	@Override
	public String getOrgUnit3() {
		return _parserDelegate.getOrgUnit3();
	}

	@Override
	public String getOrgUnit4() {
		return _parserDelegate.getOrgUnit4();
	}

	@Override
	public String getPRMD() {
		return _parserDelegate.getPRMD();
	}

	@Override
	public String getSurname() {
		return _parserDelegate.getSurname();
	}

	/*-------------------------------------------------------------------------------------*/
	/*
	 * Additional methods from ext interface
	 */
	@Override
	public String getRFC82xInternetAddress() {
		return _parserDelegate.getRFC82xInternetAddress();
	}

	@Override
	public String getIDprefix() {
		return _parserDelegate.getIDprefix();
	}

	@Override
	public String getNamePart(final NamePartKey key) {
		return (key == NamePartKey.Language) ? _language : _parserDelegate.getNamePart(key);
	}

	@Override
	public IName create(final CharSequence name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLocalServerName(final String string) {
		throw new UnsupportedOperationException();
	}

}
