package org.openntf.domino.commons;

import org.openntf.domino.commons.NameEnums.NameError;
import org.openntf.domino.commons.NameEnums.NameFormat;
import org.openntf.domino.commons.NameEnums.NamePartKey;

/**
 * The IName Interface. A common subset of getters that describes an abstract Name - required in every environment (Notes & non-Notes) (The
 * ODA-Name will extend this)
 * 
 * @author Manfred Steinsiek, Roland Praml, FOCONIS AG
 *
 */
public interface IName {

	/**
	 * Factory to create a new instance. In Java 1.8 we can use a static method in the Interface
	 */
	public enum $ {
		;
		private static IName PROTOTYPE = ServiceLocator.findApplicationService(IName.class);

		public static IName create(final CharSequence name) {
			return PROTOTYPE.create(name);
		}

		public static void setLocalServerName(final String string) {
			PROTOTYPE.setLocalServerName(string);
		}

	}

	/**
	 * The IName prototype. Always clone!
	 */

	/**
	 * Gets the RFC821 or RFC822 internet address
	 * 
	 * * A name that conforms to RFC 821 or RFC 822 is interpreted as an Internet address. Examples of Internet addresses are as follows:
	 * <ul>
	 * <li>jbg@us.acme.com
	 * <li>"John B Goode" <jbg@us.acme.com>
	 * <li>"John B Goode" <jbg@us.acme.com> (Sales) (East)
	 * </ul>
	 * 
	 * @return the Internet address, comprised of the at least the minimum RFC821 Address. If no RFC821 Address exists a blank string is
	 *         returned.
	 * 
	 * @see INameParser#getAddr821()
	 */
	public String getRFC82xInternetAddress();

	/**
	 * Returns the ID-prefix. This is often used in @Uniqe and is RPRL for "Roland Praml"
	 */
	public String getIDprefix();

	/**
	 * Returns the name part according to the specified {@link NamePartKey}
	 */
	public String getNamePart(final NamePartKey key);

	/**
	 * Returns the {@link NameFormat}
	 */
	public NameFormat getNameFormat();

	/**
	 * Returns the {@link NameError} if the name could not parsed correctly
	 */
	public NameError getNameError();

	/**
	 * Returns true if the name is hierarchical
	 */
	public boolean isHierarchical();

	/**
	 * Returns the full canonical name (CN=...)
	 */
	public String getCanonical();

	/**
	 * Returns the A= ( administration management domain name ) component
	 */
	public String getADMD();

	/**
	 * Returns the full name in abbreviated format.
	 */
	public String getAbbreviated();

	// TODO document this => JFOF-14
	public String getAddr821();

	public String getAddr822Comment1();

	public String getAddr822Comment2();

	public String getAddr822Comment3();

	public String getAddr822LocalPart();

	public String getAddr822Phrase();

	/**
	 * Returns the CN= part of the name
	 */
	public String getCommon();

	/**
	 * Returns the C= part of the name
	 */
	public String getCountry();

	/**
	 * Returns the Q= part of the name
	 */
	public String getGeneration();

	/**
	 * Returns the G= part of the name
	 */
	public String getGiven();

	/**
	 * Returns the I= part of the name
	 */
	public String getInitials();

	public String getKeyword();

	/**
	 * Returns the O= part of the name
	 */
	public String getOrganization();

	/**
	 * Returns the first organization unit of the name
	 */
	public String getOrgUnit1();

	/**
	 * Returns the second organization unit of the name
	 */
	public String getOrgUnit2();

	/**
	 * Returns the third organization unit of the name
	 */
	public String getOrgUnit3();

	/**
	 * Returns the fourth organization unit of the name
	 */
	public String getOrgUnit4();

	/**
	 * Returns the P= part of the name
	 */
	public String getPRMD();

	/**
	 * Returns the S= part of the name
	 */
	public String getSurname();

	/**
	 * Internal factory method TODO: Create a NameFactory
	 */
	public IName create(final CharSequence name);

	/**
	 * Internal method. TODO: Should be moved also to nameFactory - maybe we can omit namefactory completely.
	 */
	public void setLocalServerName(String string);

	//	/**
	//	 * Checks if the given name is member of the given collection.
	//	 * 
	//	 * @param namesOrGroups
	//	 *            a collection with Names or Groups (or roles)
	//	 * @param resolver
	//	 *            if the name is not directly listed, the request is passed to the resolver, the resolver can determine if an user is member
	//	 *            or not
	//	 * @return true if the user is member of the given group
	//	 */
	//	public boolean isMemberOf(Collection<String> namesOrGroups, IGroupResolver resolver);
}
