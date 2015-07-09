package org.openntf.domino.commons.logging;

import java.util.List;
import java.util.logging.LogRecord;

import org.openntf.domino.commons.ILoggingService;
import org.openntf.domino.commons.exception.IExceptionDetails;

public class LogRecordAdditionalInfo {

	private List<IExceptionDetails.Entry> exceptionDetails;
	private String[] lastWrappedDocs;

	public LogRecordAdditionalInfo(final LogRecord logRec) {
		exceptionDetails = ILoggingService.$.getInstance().getExceptionDetails(logRec.getThrown());
		lastWrappedDocs = ILoggingService.$.getInstance().getLastWrappedDocs();
	}

	public List<IExceptionDetails.Entry> getExceptionDetails() {
		return exceptionDetails;
	}

	public String[] getLastWrappedDocs() {
		return lastWrappedDocs;
	}

	public void writeToLog(final StringBuffer sb) {
		if (exceptionDetails != null) {
			sb.append("    Details where exception was thrown:\n");
			for (IExceptionDetails.Entry exEntry : exceptionDetails)
				sb.append("      " + exEntry.toString() + "\n");
		}
		if (lastWrappedDocs != null) {
			sb.append("    Last wrapped docs in thread:\n");
			for (int i = 0; i < lastWrappedDocs.length; i++)
				sb.append("      " + lastWrappedDocs[i] + "\n");
		}
	}

}
