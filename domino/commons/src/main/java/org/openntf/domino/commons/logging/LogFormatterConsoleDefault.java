package org.openntf.domino.commons.logging;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.openntf.domino.commons.ILoggingService;

public class LogFormatterConsoleDefault extends Formatter {

	public static LogFormatterConsoleDefault getInstance() {
		return new LogFormatterConsoleDefault();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(final LogRecord logRec) {
		StringBuffer sb = new StringBuffer();
		sb.append(LoggingAbstract.dateToString(new Date(logRec.getMillis())));
		sb.append(" [");
		sb.append(logRec.getLevel().getName());
		sb.append("]: ");
		Throwable t = logRec.getThrown();
		StackTraceElement ste = null;
		if (t != null) {
			StackTraceElement[] stes = t.getStackTrace();
			if (stes != null && stes.length > 0)
				ste = stes[0];
		}
		if (ste != null)
			sb.append(ste.getClassName() + "." + ste.getMethodName());
		else
			sb.append("***NO STACK TRACE***");
		sb.append(" - ");
		sb.append(logRec.getMessage());
		sb.append('\n');
		if (ILoggingService.INSTANCE.mayContainAdditionalInfo(logRec)) {
			LogRecordAdditionalInfo lrai = new LogRecordAdditionalInfo(logRec);
			lrai.writeToLog(sb);
		}
		if (ste != null) {
			sb.append("\n");
			sb.append("***See configured log file for full stack trace***");
			sb.append("\n");
		}
		return sb.toString();
	}
}
