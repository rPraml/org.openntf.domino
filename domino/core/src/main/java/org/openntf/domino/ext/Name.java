/**
 * 
 */
package org.openntf.domino.ext;

import org.openntf.domino.commons.NameEnums.NameError;
import org.openntf.domino.commons.NameEnums.NameFormat;
import org.openntf.domino.commons.NameEnums.NamePartKey;

/**
 * @author withersp
 * 
 *         OpenNTF extensions to Name object
 */
public interface Name {

	/**
	 * Gets groups for the person / group / server etc the Name object pertains to.<br/>
	 * The groups include the hierarchical name for the Name object, all Group entries that Name is a member of, and any OUs and O the name
	 * relates to.
	 * 
	 * <p>
	 * Sample output: CN=admin/O=Intec-PW,admin,*,*\/O=Intec-PW,LocalDomainAdmins,Domino Developers,SEAS TestRole - Y1
	 * </p>
	 * 
	 * @param serverName
	 *            String server name to check against
	 * @return Collection<String> of any Domino Directory Person or Group the Name is found in, plus generic hierarchical responses
	 * @since org.opentf.domino 5.0.0
	 */
	public java.util.Collection<String> getGroups(String serverName);

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
	 * @see Name#getAddr821()
	 */
	public String getRFC82xInternetAddress();

	/**
	 * A name is immutable, so cloning makes no sense
	 * 
	 * @return
	 */
	@Deprecated
	public org.openntf.domino.Name clone();

	public String getIDprefix();

	/**
	 * Gets the Name Part for the specified key.
	 * 
	 * @param key
	 *            Key identifying the specific mapped Name Part string to return.
	 * 
	 * @return Mapped String for the key. Empty string "" if no mapping exists.
	 */
	public String getNamePart(final NamePartKey key);

	public NameFormat getNameFormat();

	public NameError getNameError();

}
