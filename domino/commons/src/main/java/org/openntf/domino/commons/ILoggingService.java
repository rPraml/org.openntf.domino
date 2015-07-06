package org.openntf.domino.commons;

import java.util.List;
import java.util.logging.LogRecord;

import org.openntf.domino.commons.exception.IExceptionDetails;

public interface ILoggingService {

	public final ILoggingService INSTANCE = ServiceLocator.findApplicationService(ILoggingService.class);

	public static enum ConfigChangeFlag {
		CFG_UNCHANGED, CFG_UPDATED, CFG_ERROR;
	}

	public abstract ConfigChangeFlag lookForCfgChange();

	public abstract List<IExceptionDetails.Entry> getExceptionDetails(Throwable t);

	public abstract String[] getLastWrappedDocs();

	public abstract boolean mayContainAdditionalInfo(LogRecord logRec);

	public abstract String replacePatternPlaceHolders(String where);

	public abstract String[] getCurrentUserAndDB(Throwable exception, boolean userRequired, boolean dbRequired) throws Exception;

}
