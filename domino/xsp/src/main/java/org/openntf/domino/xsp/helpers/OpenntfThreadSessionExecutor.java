package org.openntf.domino.xsp.helpers;

import lotus.domino.NotesException;

import org.eclipse.core.runtime.Status;
import org.openntf.domino.Session;
import org.openntf.domino.commons.exception.IExceptionDetails;
import org.openntf.domino.utils.ODAUtils;

import com.ibm.domino.xsp.module.nsf.ThreadSessionExecutor;

public class OpenntfThreadSessionExecutor<IStatus> extends ThreadSessionExecutor<IStatus> {

	public OpenntfThreadSessionExecutor() {
	}

	@SuppressWarnings("unchecked")
	protected IStatus run(final Session session) throws Exception {
		try {
			return run(session.getFactory().toLotus(session));
		} catch (NotesException e) {
			ODAUtils.handleException(e, (IExceptionDetails) this);
			return (IStatus) Status.CANCEL_STATUS;
		}
	}

	@Override
	protected IStatus run(final lotus.domino.Session arg0) throws Exception {
		return super.run();
	}

}
