package org.openntf.domino.commons.logging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

import org.openntf.domino.commons.types.ExceptionDetails;

/**
 * Introduces a highly configurable new logging mechanism. (Details are to be found in logconfig.properties.) If no configuration property
 * file is found, logging is statically initialized as before (as a fallback solution ). If the configuration is changed, logging will
 * update itself within 1 minute.
 * 
 * @author Steinsiek
 * 
 */
public abstract class LoggingAbstract {

	private static ThreadLocal<SimpleDateFormat> sdfISO = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	public static String dateToString(final Date value) {
		return sdfISO.get().format(value);
	}

	protected static LoggingAbstract _theLogger;

	protected LoggingAbstract() {
	}

	public static LoggingAbstract getInstance() {
		return _theLogger;
	}

	private LogConfig _activeConfig = null;
	private Timer _supervisor = null;
	private static final long _supervisorInterval = 60000;	// 1 minute

	public void startUp() throws IOException {
		if (!activateCfgFromProperties()) {
			System.err.println("Logging: Couldn't initialize from logging properties; activating fallback ...");
			startUpFallback();
			return;
		}
		System.out.println("Logging: LogConfig successfully initialized from logging properties");

		_supervisor = new Timer("LoggingSupervisor", true);
		_supervisor.schedule(new TimerTask() {
			@Override
			public void run() {
				LoggingAbstract.getInstance().lookForCfgChange();
			}
		}, _supervisorInterval, _supervisorInterval);
	}

	protected boolean activateCfgFromProperties() {
		LogConfig logCfg;
		Properties props = loadCfgProperties();
		if (props == null || (logCfg = LogConfig.fromProperties(props)) == null)
			return false;
		if (!createFilterHandlers(logCfg))
			return false;
		if (_activeConfig == null)	// Necessary only if starting up
			if (!setPropsActive(props))
				return false;
		activateFilterHandlers(logCfg);
		_activeConfig = logCfg;
		return true;
	}

	protected abstract Properties loadCfgProperties();

	protected abstract boolean setPropsActive(Properties props);

	private boolean createFilterHandlers(final LogConfig logCfg) {
		try {
			for (LogConfig.L_LogFilterHandler llfh : logCfg._logFilterHandlers.values())
				LogFilterHandler.getInitializedInstance(llfh, _activeConfig);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			for (LogConfig.L_LogFilterHandler llfh : logCfg._logFilterHandlers.values())
				if (llfh._myHandler != null) {
					llfh._myHandler.finishUp();
					llfh._myHandler = null;
				}
			return false;
		}
	}

	private void activateFilterHandlers(final LogConfig logCfg) {
		int sz = (_activeConfig == null) ? 0 : _activeConfig._logFilterHandlers.size();
		LogFilterHandler[] oldLFHs = new LogFilterHandler[sz];
		if (_activeConfig != null) {
			int i = 0;
			for (LogConfig.L_LogFilterHandler llfh : _activeConfig._logFilterHandlers.values())
				oldLFHs[i++] = llfh._myHandler;
		}
		for (LogConfig.L_LogFilterHandler llfh : logCfg._logFilterHandlers.values())
			llfh._myHandler.activateYourself(oldLFHs);
	}

	protected abstract void startUpFallback() throws IOException;

	protected static enum ConfigChangeFlag {
		CFG_UNCHANGED, CFG_UPDATED, CFG_ERROR;
	}

	protected abstract ConfigChangeFlag lookForCfgChange();

	public abstract List<ExceptionDetails.Entry> getExceptionDetails(Throwable t);

	public abstract String[] getLastWrappedDocs();

	public abstract boolean mayContainAdditionalInfo(LogRecord logRec);

	public abstract String replacePatternPlaceHolders(String where);

	public abstract LogFormulaCondHandlerIF getFormulaCondHandler(String formulaCond) throws Exception;

	public abstract String[] getCurrentUserAndDB(Throwable exception, boolean userRequired, boolean dbRequired) throws Exception;

	static boolean _verbose = false;	// For testing

	public static void setVerbose(final boolean how) {
		_verbose = how;
	}
}
