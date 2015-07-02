package org.openntf.domino.logging.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import org.openntf.domino.Database;
import org.openntf.domino.Session;
import org.openntf.domino.WrapperFactory;
import org.openntf.domino.commons.exception.IExceptionDetails;
import org.openntf.domino.commons.logging.LogFormulaCondHandlerIF;
import org.openntf.domino.commons.logging.LoggingAbstract;
import org.openntf.domino.exceptions.OpenNTFNotesException;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

/**
 * Implementation of LoggingAbstract, where the Properties are drawn from a file.
 * 
 * @author Steinsiek
 * 
 */
public class Logging extends LoggingAbstract {

	public static LoggingAbstract getInstance() {
		if (LoggingAbstract._theLogger == null)
			LoggingAbstract._theLogger = new Logging();
		return LoggingAbstract._theLogger;
	}

	private Logging() {
	}

	private static String _logConfigPropFile = null;

	public void setLogConfigPropFile(final String how) {
		_logConfigPropFile = how;
	}

	@Override
	protected Properties loadCfgProperties() {
		File logConfigFile = logCfgFilePrecheck();
		if (logConfigFile == null)
			return null;
		try {
			FileInputStream fis = new FileInputStream(logConfigFile);
			Properties props = new Properties();
			props.load(fis);
			fis.close();
			return props;
		} catch (Exception e) {
			System.err.println("Logging.loadCfgProperties: Exception " + e.getClass().getName() + ":");
			e.printStackTrace();
			return null;
		}
	}

	private File logCfgFilePrecheck() {
		if (_logConfigPropFile == null)
			_logConfigPropFile = Factory.getDataPath() + "/IBM_TECHNICAL_SUPPORT/org.openntf.domino.logging.logconfig.properties";
		File ret = new File(_logConfigPropFile);
		String errMsg = null;
		if (!ret.exists())
			errMsg = "not found";
		else if (!ret.isFile())
			errMsg = "isn't a normal file";
		if (errMsg == null)
			return ret;
		System.err.println("Logging.logCfgFilePrecheck: File '" + _logConfigPropFile + "' " + errMsg);
		return null;
	}

	@Override
	protected void startUpFallback() throws IOException {
		String pattern = Factory.getDataPath() + "/IBM_TECHNICAL_SUPPORT/org.openntf.%u.%g.log";
		Logger oodLogger = Logger.getLogger("org.openntf.domino");
		oodLogger.setLevel(Level.WARNING);

		DefaultFileHandler dfh = new DefaultFileHandler(pattern, 50000, 100, true);
		dfh.setFormatter(new FileFormatter());
		dfh.setLevel(Level.WARNING);
		oodLogger.addHandler(dfh);

		DefaultConsoleHandler dch = new DefaultConsoleHandler();
		dch.setFormatter(new ConsoleFormatter());
		dch.setLevel(Level.WARNING);
		oodLogger.addHandler(dch);

		OpenLogHandler olh = new OpenLogHandler();
		olh.setLogDbPath("OpenLog.nsf");
		olh.setLevel(Level.WARNING);
		oodLogger.addHandler(olh);

		LogManager.getLogManager().addLogger(oodLogger);
	}

	private long _propFileLh;
	private long _propFileCRC;

	private boolean getCfgPropFileNumbers() {
		long fileLh = 0;
		long fileCRC = 0;
		File f = logCfgFilePrecheck();
		if (f == null)
			return false;
		try {
			FileInputStream fis = new FileInputStream(f);
			CRC32 crc = new CRC32();
			byte[] buff = new byte[16384];
			int got;
			while ((got = fis.read(buff)) > 0) {
				fileLh += got;
				crc.update(buff, 0, got);
			}
			fis.close();
			fileCRC = crc.getValue();
		} catch (Exception e) {
			System.err.println("Logging.getCfgPropFileNumbers: " + "Exception " + e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		_propFileLh = fileLh;
		_propFileCRC = fileCRC;
		return true;
	}

	@Override
	protected boolean setPropsActive(final Properties props) {
		return getCfgPropFileNumbers();
	}

	@Override
	protected ConfigChangeFlag lookForCfgChange() {
		long oldFileLh = _propFileLh;
		long oldFileCRC = _propFileCRC;
		if (!getCfgPropFileNumbers())
			return ConfigChangeFlag.CFG_ERROR;
		if (_propFileLh == oldFileLh && _propFileCRC == oldFileCRC)
			return ConfigChangeFlag.CFG_UNCHANGED;
		if (activateCfgFromProperties()) {
			System.out.println("Logging: Updated LogConfig from changed PropertyFile");
			return ConfigChangeFlag.CFG_UPDATED;
		}
		_propFileLh = oldFileLh;
		_propFileCRC = oldFileCRC;
		System.err.println("Logging: Couldn't update LogConfig from changed PropertyFile because of errors");
		return ConfigChangeFlag.CFG_ERROR;
	}

	@Override
	public List<IExceptionDetails.Entry> getExceptionDetails(final Throwable t) {
		return (t instanceof OpenNTFNotesException) ? ((OpenNTFNotesException) t).getExceptionDetails() : null;
	}

	@Override
	public String[] getLastWrappedDocs() {
		WrapperFactory wf = Factory.getWrapperFactory_unchecked();
		return (wf == null) ? null : wf.getLastWrappedDocsInThread();
	}

	@Override
	public boolean mayContainAdditionalInfo(final LogRecord logRec) {
		return logRec.getThrown() instanceof OpenNTFNotesException;
	}

	@Override
	public String replacePatternPlaceHolders(final String where) {
		return where.replace("<notesdata>", Factory.getDataPath());
	}

	@Override
	public String[] getCurrentUserAndDB(final Throwable exception, final boolean userRequired, final boolean dbRequired) throws Exception {
		String[] ret = new String[2];
		/* Try first to get from ExceptionDetails - that's cheap */
		searchInExceptionDetails(ret, exception);
		if ((!userRequired || ret[0] != null) && (!dbRequired || ret[1] != null))
			return ret;

		/* We have to ask Session - that's not cheap */
		Session sess = Factory.getSession_unchecked(SessionType.CURRENT);
		if (sess == null) // then we can't evaluate the condition
			throw new Exception("Factory.getSession_unchecked(Type=CURRENT) returned null");
		if (userRequired)
			ret[0] = sess.getEffectiveUserName();
		if (dbRequired)
			ret[1] = sess.getCurrentDatabase().getApiPath();
		return ret;
	}

	private void searchInExceptionDetails(final String[] userDB, final Throwable exception) {
		List<IExceptionDetails.Entry> excds = getExceptionDetails(exception);
		if (excds == null)
			return;
		for (IExceptionDetails.Entry detail : excds)
			if (Session.class.isAssignableFrom(detail.getSource()))
				userDB[0] = detail.getMessage();
			else if (Database.class.isAssignableFrom(detail.getSource()))
				userDB[1] = detail.getMessage();
	}

	@Override
	public LogFormulaCondHandlerIF getFormulaCondHandler(final String formulaCond) throws Exception {
		return LogFormulaCondHandler.getInstance(formulaCond);
	}
}
