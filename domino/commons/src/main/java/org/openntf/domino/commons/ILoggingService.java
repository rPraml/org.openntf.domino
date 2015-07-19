package org.openntf.domino.commons;

import java.util.List;
import java.util.logging.LogRecord;

import org.openntf.domino.commons.exception.IExceptionDetails;

/**
 * TODO: document this.
 * 
 * @deprecate: It is not yet clear, how this fits in spring boot logging - so this interface is temporary marked as deprecated
 */
public interface ILoggingService {

	/**
	 * Factory to create a new instance. In Java 1.8 we can use a static method in the Interface
	 */
	public enum $ {
		;
		private static final ILoggingService INSTANCE = ServiceLocator.findApplicationService(ILoggingService.class);

		public static ILoggingService getInstance() {
			return INSTANCE;
		}
	}

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
