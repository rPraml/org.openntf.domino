package org.openntf.domino.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.openntf.domino.commons.exception.IExceptionDetails;

public class OpenNTFNotesException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public OpenNTFNotesException() {
		super();
	}

	/**
	 * Constructor with message
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later retrieval by the Throwable.getMessage() method.
	 */
	public OpenNTFNotesException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            the cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 */

	public OpenNTFNotesException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 *            the detail message. The detail message is saved for later retrieval by the Throwable.getMessage() method.
	 * @param cause
	 *            the cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 */
	public OpenNTFNotesException(final String message, final Throwable cause) {
		super(message, cause);
	}

	// in the most cases we will have only one exDetail.
	private transient IExceptionDetails IExceptionDetails_;
	// the List is only used if we have more than once ExceptionDetalis
	private transient List<IExceptionDetails> moreIExceptionDetails_;

	/**
	 * This method is called if we bubble up and reach an other object that implements also IExceptionDetails
	 * 
	 */
	public void addExceptionDetails(final IExceptionDetails ed) {
		if (IExceptionDetails_ == null) {
			IExceptionDetails_ = ed;
			return;
		}
		if (moreIExceptionDetails_ == null) {
			moreIExceptionDetails_ = new ArrayList<IExceptionDetails>();
		}
		moreIExceptionDetails_.add(ed);

	}

	//public IExceptionDetails getIExceptionDetails() {
	//	return IExceptionDetails_;
	//}

	//public void setIExceptionDetails(final IExceptionDetails ed) {
	//	this.IExceptionDetails_ = ed;
	//}

	public OpenNTFNotesException(final String message, final Throwable cause, final IExceptionDetails ed) {
		this(message, cause);
		IExceptionDetails_ = ed;
	}

	public OpenNTFNotesException(final Throwable cause, final IExceptionDetails ed) {
		this(cause);
		IExceptionDetails_ = ed;
	}

	public List<IExceptionDetails.Entry> getExceptionDetails() {
		if (IExceptionDetails_ == null)
			return null;
		ArrayList<IExceptionDetails.Entry> ret = new ArrayList<IExceptionDetails.Entry>();
		try {
			IExceptionDetails_.fillExceptionDetails(ret);
		} catch (Throwable t) {
			// we will ignore ANY error that occurs while gathering more information (it could happen that the DB is closed now and you get stuck in a loop)
		}

		if (moreIExceptionDetails_ != null) {
			for (IExceptionDetails med : moreIExceptionDetails_) {
				try {
					med.fillExceptionDetails(ret);
				} catch (Throwable t) {

				}
			}
		}
		return ret;
	}

}