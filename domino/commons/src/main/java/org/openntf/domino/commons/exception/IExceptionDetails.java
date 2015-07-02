/**
 * 
 */
package org.openntf.domino.commons.exception;

import java.util.List;

/**
 * The interface ExceptionDetails; useful to add further information to an OpenNTFNotesException. Of course, Document, Item, Database,
 * Session, ... implement this interface
 * 
 */
public interface IExceptionDetails {

	/**
	 * This method will provide additional exception details
	 * 
	 */
	public void fillExceptionDetails(List<Entry> result);

	/**
	 * A Entry to provide more Exception details.<br>
	 * <ul>
	 * <li>Session: userName</li>
	 * <li>Database: apiPath</li>
	 * <li>Document: ReplicaID:..., UNID:..., NoteId:..., Form:...</li>
	 * </ul>
	 * 
	 * @author Roland Praml, FOCONIS AG
	 * 
	 */
	public static class Entry {
		final Class<?> sourceCls;
		final String msg;

		public Entry(final Object source, final String msg) {
			sourceCls = source == null ? Void.class : source.getClass();
			this.msg = msg;
		}

		@Override
		public String toString() {
			return sourceCls.getName() + "=" + msg;
		}

		public Class<?> getSource() {
			return sourceCls;
		}

		public String getMessage() {
			return msg;
		}
	}

}
