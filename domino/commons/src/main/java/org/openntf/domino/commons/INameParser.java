package org.openntf.domino.commons;

import org.openntf.domino.commons.NameEnums.NameError;
import org.openntf.domino.commons.NameEnums.NameFormat;
import org.openntf.domino.commons.NameEnums.NamePartKey;

public interface INameParser {

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

	public String getIDprefix();

	public String getNamePart(final NamePartKey key);

	public NameFormat getNameFormat();

	public NameError getNameError();

	public boolean isHierarchical();

	public String getCanonical();

	public String getADMD();

	public String getAbbreviated();

	public String getAddr821();

	public String getAddr822Comment1();

	public String getAddr822Comment2();

	public String getAddr822Comment3();

	public String getAddr822LocalPart();

	public String getAddr822Phrase();

	public String getCommon();

	public String getCountry();

	public String getGeneration();

	public String getGiven();

	public String getInitials();

	public String getKeyword();

	public String getOrganization();

	public String getOrgUnit1();

	public String getOrgUnit2();

	public String getOrgUnit3();

	public String getOrgUnit4();

	public String getPRMD();

	public String getSurname();

}
