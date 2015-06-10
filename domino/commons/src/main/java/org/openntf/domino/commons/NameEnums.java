/**
 * 
 */
package org.openntf.domino.commons;

public enum NameEnums {

	;

	public static enum NamePartKey {
		Abbreviated, Addr821, Addr822Comment1, Addr822Comment2, Addr822Comment3, Addr822LocalPart, Addr822Phrase, ADMD, Canonical, Common,
		Country, Generation, Given, Initials, Keyword, Language, Organization, OrgUnit1, OrgUnit2, OrgUnit3, OrgUnit4, PRMD, Surname,
		IDprefix, SourceString;

		@Override
		public String toString() {
			return NamePartKey.class.getName() + ": " + this.name();
		}

		public String getInfo() {
			return this.getDeclaringClass() + "." + this.getClass() + ":" + this.name();
		}
	};

	public static enum NameFormat {
		/**
		 * NameFormat unknown or not supplied by implementation
		 */
		UNKNOWN(false, false),
		/**
		 * A name without a slash, not containing a "CN="
		 */
		FLAT(false, false),
		/**
		 * Something a priori invalid, like a blank string
		 */
		FLATERROR(false, true),
		/**
		 * A Domino hierarchical name
		 */
		DOMINO(true, false),
		/**
		 * A hierarchical name
		 */
		HIERARCHICAL(true, false),
		/**
		 * An extended hierarchical name with components like /A=ADMD, /Q=Generation, /G=Given, /I=Initials, /P=PRMD, /S=Surname
		 */
		HIERARCHICALEX(true, false),
		/**
		 * A hierarchical name containing errors
		 */
		HIERARCHICALERROR(true, true),
		/**
		 * An RFC822 name (Mail address)
		 */
		RFC822(false, false),
		/**
		 * An invalid RFC822 address
		 */
		RFC822ERROR(false, true);

		private final boolean _hierarchical;
		private final boolean _error;

		private NameFormat(final boolean hierarchical, final boolean error) {
			_hierarchical = hierarchical;
			_error = error;
		}

		public boolean isHierarchical() {
			return _hierarchical;
		}

		public boolean isError() {
			return _error;
		}

	}

	public static enum NameError {
		/**
		 * No error information available
		 */
		NOT_AVAILABLE,
		/**
		 * Blank string on input
		 */
		EMPTY_NAME,
		/**
		 * General syntax error, e.g. last sign = '@'
		 */
		GENERAL_SYNTAX_ERROR,
		/**
		 * Contains parts with XX= as well as parts without, e.g. CN=J Smith/Dev
		 */
		MIXED_HIERARCHICAL,
		/**
		 * Unidentifiable X400 prefix, e.g. OU5=
		 */
		UNKNOWN_PART,
		/**
		 * One part occurs at least twice
		 */
		DOUBLE_PART,
		/**
		 * The first part of a hierarchical address mustn't be empty
		 */
		EMPTY_FIRST_PART,
		/**
		 * There are only 4 OUs allowed
		 */
		TWO_MANY_OUS,
		/**
		 * Invalid RFC822 Internet address
		 */
		INVALID_MAILADDR,
		/**
		 * Invalid RFC822 expression, e.g. missing bracket, missing < or >, ...
		 */
		INVALID_RFC822,
		/**
		 * No error
		 */
		NO_ERROR
	}

}
