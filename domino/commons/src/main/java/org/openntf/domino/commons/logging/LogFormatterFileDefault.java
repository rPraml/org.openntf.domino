package org.openntf.domino.commons.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFormatterFileDefault extends Formatter {

	public static LogFormatterFileDefault getInstance() {
		return new LogFormatterFileDefault();
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
		sb.append("Log from " + logRec.getLoggerName());
		sb.append('\n');
		Throwable t = logRec.getThrown();
		StackTraceElement ste = null;
		if (t != null) {
			StackTraceElement[] stes = t.getStackTrace();
			if (stes != null && stes.length > 0)
				ste = stes[0];
		}
		sb.append("      ");
		sb.append(logRec.getMessage());
		boolean levelSevere = (logRec.getLevel().intValue() >= Level.SEVERE.intValue());
		if (ste != null || levelSevere)
			sb.append(" - ");
		if (ste != null)
			sb.append(ste.getClassName() + "." + ste.getMethodName());
		else if (levelSevere)
			sb.append("***NO STACK TRACE***");
		sb.append('\n');
		if (LoggingAbstract.getInstance().mayContainAdditionalInfo(logRec)) {
			LogRecordAdditionalInfo lrai = new LogRecordAdditionalInfo(logRec);
			lrai.writeToLog(sb);
		}
		if (t != null && ste != null) { // Of course superfluous: t!=null, but otherwise we get a warning
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			sb.append("    ");
			sb.append(sw.toString().replace("\r\n", "\n"));
			sb.append("\n");
		}
		return sb.toString();
	}
}
