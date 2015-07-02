package org.openntf.domino.ext;

/**
 * Interface for collections that are containing a List of NoteIDs
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public interface NoteIDContainer {
	/**
	 * Returns an array with NoteIDs
	 */
	public int[] getNoteIDs();
}
