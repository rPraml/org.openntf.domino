/**
 * 
 */
package org.openntf.domino.ext;

import org.openntf.domino.commons.IDateTime;

/**
 * @author nfreeman
 * 
 *         OpenNTF extensions to DateTime class
 * 
 */
public interface DateTime extends IDateTime {
	// stub.. all methods are in IDateTime now

	@Override
	public org.openntf.domino.DateTime clone();
}
