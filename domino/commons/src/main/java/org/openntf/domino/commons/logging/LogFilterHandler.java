package org.openntf.domino.commons.logging;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.openntf.domino.commons.ILoggingService;

public class LogFilterHandler extends Handler {

	private class L_HandlerEx {
		Handler _handler;
		boolean _mightPublish;
		boolean _invalidated;

		L_HandlerEx(final Handler h) {
			_handler = h;
			_mightPublish = true;
			_invalidated = false;
		}
	}

	private class L_HandlerUpdateEntry {
		LogHandlerUpdateIF _oldHandlerUIF;
		Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> _newHandlerEnt;
		L_HandlerEx _oldHex;
		LogHandlerConfigIF _newHandlerConfig;
		LogHandlerConfigIF _oldHandlerConfig;
		boolean _useDefaultFormatter;
		Formatter _newFormatter;

		L_HandlerUpdateEntry(final LogHandlerUpdateIF oldHandlerUIF, final Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> newHandlerEnt,
				final L_HandlerEx oldHex, final LogHandlerConfigIF newHandlerConfig, final LogHandlerConfigIF oldHandlerConfig,
				final boolean useDefaultFormatter, final Formatter newFormatter) {
			_oldHandlerUIF = oldHandlerUIF;
			_newHandlerEnt = newHandlerEnt;
			_oldHex = oldHex;
			_newHandlerConfig = newHandlerConfig;
			_oldHandlerConfig = oldHandlerConfig;
			_useDefaultFormatter = useDefaultFormatter;
			_newFormatter = newFormatter;
		}
	}

	private LogConfig.L_LogFilterHandler _myConfigLFH;
	private HashMap<LogConfig.L_LogHandler, L_HandlerEx> _myHandlers;
	private boolean _activated = false;
	private Set<L_HandlerUpdateEntry> _handlerUpdateSet = null;

	public LogFilterHandler() {
		super();
		_myHandlers = new HashMap<LogConfig.L_LogHandler, L_HandlerEx>();
	}

	public static LogFilterHandler getInitializedInstance(final LogConfig.L_LogFilterHandler lfh, final LogConfig oldConfig)
			throws Exception {
		LogFilterHandler ret = new LogFilterHandler();
		ret.startUp(lfh, oldConfig);
		return ret;
	}

	public void startUp(final LogConfig.L_LogFilterHandler lfh, final LogConfig oldConfig) throws Exception {
		try {
			_myConfigLFH = lfh;
			_myConfigLFH._myHandler = this;
			setUpMyLevel();
			getMyHandlers();
			setUpMyHandlers(oldConfig);
		} catch (Exception e) {
			finishUp();
			throw e;
		}
	}

	private void setUpMyLevel() {
		Level l = _myConfigLFH._defaultLevel;
		for (LogConfig.L_LogFilterHandler.L_LogFilterConfigEntry fce : _myConfigLFH._logFCEList)
			if (fce._level.intValue() < l.intValue())
				l = fce._level;
		setLevel(l);
	}

	private void getMyHandlers() {
		_myHandlers.clear();
		for (int i = 0; i < _myConfigLFH._defaultHandlers.length; i++)
			_myHandlers.put(_myConfigLFH._defaultHandlers[i], null);
		for (LogConfig.L_LogFilterHandler.L_LogFilterConfigEntry fce : _myConfigLFH._logFCEList)
			for (int i = 0; i < fce._logHandlerObjs.length; i++)
				_myHandlers.put(fce._logHandlerObjs[i], null);
	}

	private void setUpMyHandlers(final LogConfig oldConfig) throws Exception {
		Set<Map.Entry<LogConfig.L_LogHandler, L_HandlerEx>> handlerSet = _myHandlers.entrySet();
		for (Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> handlerEnt : handlerSet) {
			Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> oldHandlerEnt = null;
			do {
				if (oldConfig == null)
					break;
				LogConfig.L_LogFilterHandler oldLLFH = oldConfig._logFilterHandlers.get(_myConfigLFH._myName);
				if (oldLLFH == null)
					break;
				LogConfig.L_LogHandler oldLH = oldConfig._logHandlers.get(handlerEnt.getKey()._handlerName);
				if (oldLH == null)
					break;
				Set<Map.Entry<LogConfig.L_LogHandler, L_HandlerEx>> oldHandEnts = oldLLFH._myHandler._myHandlers.entrySet();
				for (Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> me : oldHandEnts)
					if (me.getKey() == oldLH) {
						oldHandlerEnt = me;
						break;
					}
			} while (false);
			setUpHandler(handlerEnt, oldHandlerEnt);
		}
	}

	private void setUpHandler(final Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> handlerEnt,
			final Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> oldHandlerEnt) throws Exception {
		if (oldHandlerEnt != null)
			if (tryUpdateHandler(handlerEnt, oldHandlerEnt))
				return;
		LogConfig.L_LogHandler handlerCfgEnt = handlerEnt.getKey();
		boolean useDefaultFormatter = (handlerCfgEnt._formatterClass == null);
		try {
			Handler newHandler = (Handler) handlerCfgEnt._handlerGetInstance
					.invoke(null, handlerCfgEnt._handlerConfig, useDefaultFormatter);
			handlerEnt.setValue(new L_HandlerEx(newHandler));
			if (!useDefaultFormatter)
				newHandler.setFormatter((Formatter) handlerCfgEnt._formatterGetInstance.invoke(null));
		} catch (Exception e) {
			System.err.println("Logging: Error setting up Handler " + handlerCfgEnt._handlerClassName);
			throw e;
		}
	}

	private boolean tryUpdateHandler(final Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> handlerEnt,
			final Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> oldHandlerEnt) {
		LogConfig.L_LogHandler handlerCfgEnt = handlerEnt.getKey();
		LogConfig.L_LogHandler oldHandlerCfgEnt = oldHandlerEnt.getKey();
		if (handlerCfgEnt._handlerClass != oldHandlerCfgEnt._handlerClass)
			return false;
		L_HandlerEx oldHex = oldHandlerEnt.getValue();
		if (!(oldHex._handler instanceof LogHandlerUpdateIF))
			return false;
		LogHandlerUpdateIF oldHandlerUIF = (LogHandlerUpdateIF) oldHex._handler;
		if (!oldHandlerUIF.mayUpdateYourself(handlerCfgEnt._handlerConfig, oldHandlerCfgEnt._handlerConfig))
			return false;
		boolean useDefaultFormatter = (handlerCfgEnt._formatterClass == null);
		Formatter formatter = null;
		if (!useDefaultFormatter && handlerCfgEnt._formatterClass != oldHandlerCfgEnt._formatterClass)
			try {
				formatter = (Formatter) handlerCfgEnt._formatterGetInstance.invoke(null);
			} catch (Exception e) {
				System.err.println("Unexpected Exception " + e.getClass().getName() + " in tryUpdateHandler:");
				e.printStackTrace();
				return false;
			}
		if (_handlerUpdateSet == null)
			_handlerUpdateSet = new HashSet<L_HandlerUpdateEntry>();
			_handlerUpdateSet.add(new L_HandlerUpdateEntry(oldHandlerUIF, handlerEnt, oldHex, handlerCfgEnt._handlerConfig,
					oldHandlerCfgEnt._handlerConfig, useDefaultFormatter, formatter));
			return true;
	}

	void activateYourself(final LogFilterHandler[] oldLFHs) {
		if (_handlerUpdateSet != null) {
			for (L_HandlerUpdateEntry hue : _handlerUpdateSet) {
				hue._oldHex._invalidated = true;
				L_HandlerEx newHex = new L_HandlerEx(hue._oldHex._handler);
				hue._newHandlerEnt.setValue(newHex);
				hue._oldHex._handler = null;
				hue._oldHandlerUIF.doUpdateYourself(hue._newHandlerConfig, hue._oldHandlerConfig, hue._useDefaultFormatter,
						hue._newFormatter);
			}
			_handlerUpdateSet = null;
		}
		_myConfigLFH._loggers = new Logger[_myConfigLFH._loggerNames.length];
		for (int i = 0; i < _myConfigLFH._loggerNames.length; i++)
			_myConfigLFH._loggers[i] = activateOneLogger(_myConfigLFH._loggerNames[i], oldLFHs);
		_activated = true;
	}

	private Logger activateOneLogger(final String loggerName, final LogFilterHandler[] oldLFHs) {
		Logger l = Logger.getLogger(loggerName);
		l.setLevel(getLevel());
		for (int i = 0; i < oldLFHs.length; i++) {
			oldLFHs[i].close();
			l.removeHandler(oldLFHs[i]);
		}
		l.addHandler(this);
		l.setUseParentHandlers(false);
		LogManager.getLogManager().addLogger(l);
		return l;
	}

	public void finishUp() {
		if (_myConfigLFH._loggers != null)
			for (int i = 0; i < _myConfigLFH._loggers.length; i++)
				_myConfigLFH._loggers[i].removeHandler(this);
		close();
		Set<Map.Entry<LogConfig.L_LogHandler, L_HandlerEx>> handlerSet = _myHandlers.entrySet();
		for (Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> handlerEnt : handlerSet)
			handlerEnt.setValue(null);
		_activated = false;
	}

	@Override
	public void close() {
		//		if (!_startUpDone)
		//			return;
		Collection<L_HandlerEx> collHand = _myHandlers.values();
		for (L_HandlerEx h : collHand)
			if (h != null && h._handler != null)
				h._handler.close();
	}

	@Override
	public void flush() {
		if (!_activated)
			return;
		Collection<L_HandlerEx> collHand = _myHandlers.values();
		for (L_HandlerEx h : collHand)
			if (h != null && h._handler != null)
				h._handler.flush();
	}

	@Override
	public synchronized void publish(final LogRecord logRec) {
		if (!_activated)
			return;
		if (publishing_.get() == Boolean.TRUE) // this prevents loopig
			return;
		publishing_.set(Boolean.TRUE);
		try {
			if (LoggingAbstract._verbose)
				System.out.println("Logging: " + logRec.getLoggerName() + " | " + logRec.getLevel().getName() + ": " + logRec.getMessage());

			resetMightPublish(logRec.getLevel());
			publishDefault(logRec);
			Map<String, Object> contextMap = null;
			for (LogConfig.L_LogFilterHandler.L_LogFilterConfigEntry fce : _myConfigLFH._logFCEList)
				contextMap = publishFCE(fce, logRec, contextMap);
		} finally {
			publishing_.set(Boolean.FALSE);
		}
	}

	private static ThreadLocal<Boolean> publishing_ = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return Boolean.FALSE;
		}
	};

	private void resetMightPublish(final Level l) {
		Set<Map.Entry<LogConfig.L_LogHandler, L_HandlerEx>> handlerSet = _myHandlers.entrySet();
		for (Map.Entry<LogConfig.L_LogHandler, L_HandlerEx> handlerEnt : handlerSet) {
			Level handlerMinLevel = handlerEnt.getKey()._minimalLevel;
			handlerEnt.getValue()._mightPublish = (handlerMinLevel == null || l.intValue() >= handlerMinLevel.intValue());
		}
	}

	private void publishDefault(final LogRecord logRec) {
		if (logRec.getLevel().intValue() < _myConfigLFH._defaultLevel.intValue())
			return;
		for (int i = 0; i < _myConfigLFH._defaultHandlers.length; i++)
			publishConditionally(_myConfigLFH._defaultHandlers[i], logRec);
	}

	private void publishConditionally(final LogConfig.L_LogHandler lHandler, final LogRecord logRec) {
		L_HandlerEx hex = _myHandlers.get(lHandler);
		if (hex._mightPublish && !hex._invalidated) {
			if (LoggingAbstract._verbose)
				System.out.println("Publishing with Handler " + lHandler._handlerName);
			hex._handler.publish(logRec);
			hex._mightPublish = false;
		}
	}

	private Map<String, Object> publishFCE(final LogConfig.L_LogFilterHandler.L_LogFilterConfigEntry fce, final LogRecord logRec,
			Map<String, Object> contextMap) {
		if (!logRec.getLoggerName().startsWith(fce._logPrefix) || // Trivial preconditions
				logRec.getLevel().intValue() < fce._level.intValue())
			return contextMap;
		/* If FCE entry has expired meanwhile, let next config update throw it away. */
		if (fce._validUntil != null && logRec.getMillis() > fce._validUntil.getTime())
			return contextMap;
		/* Look for a handler that hasn't yet published the record in question */
		int i;
		for (i = 0; i < fce._logHandlerObjs.length; i++)
			if (_myHandlers.get(fce._logHandlerObjs[i])._mightPublish)
				break;
		if (i >= fce._logHandlerObjs.length)
			return contextMap;
		/* Finally, inspect complex condition */
		if (fce._condHandler != null) {
			if (contextMap == null) {
				contextMap = new HashMap<String, Object>();
				contextMap.put(LogConfig.cLoggerName, logRec.getLoggerName());
				contextMap.put(LogConfig.cLogMessage, logRec.getMessage());
			}
			if (fce._condContUserName || fce._condContDBPath)
				if (!extendContextByUserDB(fce, contextMap, logRec.getThrown()))
					return contextMap;
			try {
				List<Object> res = fce._condHandler.solve(Locale.ENGLISH, contextMap);
				Object o = (res == null || res.size() != 1) ? null : res.get(0);
				if (!(o instanceof Boolean) || !((Boolean) o))
					return contextMap;
			} catch (Exception e) {
				System.err.println("LogFilterHandler: Exception during condition check:");
				e.printStackTrace();
				return contextMap;
			}
		}
		/* ... and now we are ready to publish */
		for (i = 0; i < fce._logHandlerObjs.length; i++)
			publishConditionally(fce._logHandlerObjs[i], logRec);
		return contextMap;
	}

	/**
	 * This method tries to insert the current context
	 * 
	 * @param fce
	 * @param publishDocMap
	 * @param exception
	 * @return
	 */
	private boolean extendContextByUserDB(final LogConfig.L_LogFilterHandler.L_LogFilterConfigEntry fce,
			final Map<String, Object> contextMap, final Throwable exception) {

		boolean userRequired = fce._condContUserName && !contextMap.containsKey(LogConfig.cUserName);
		boolean dbRequired = fce._condContDBPath && !contextMap.containsKey(LogConfig.cDBPath);
		if (!userRequired && !dbRequired)
			return true;
		try {
			String[] userDB = ILoggingService.INSTANCE.getCurrentUserAndDB(exception, userRequired, dbRequired);
			if (userRequired)
				contextMap.put(LogConfig.cUserName, userDB[0]);
			if (dbRequired)
				contextMap.put(LogConfig.cDBPath, userDB[1]);
		} catch (Exception e) {
			System.err.println("[WARNING] LogFilterHandler: " + e.getMessage());
			return false;
		}
		return true;
	}

}
