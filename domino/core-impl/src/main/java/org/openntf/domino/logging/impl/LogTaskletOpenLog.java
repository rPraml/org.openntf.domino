package org.openntf.domino.logging.impl;

import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

@SuppressWarnings("serial")
public class LogTaskletOpenLog implements Runnable {

	public LogTaskletOpenLog() {
		super();
	}

	@Override
	public void run() {
		// TODO RPr Review this!
		while (true) {
			if (Thread.interrupted())
				return;
			LogGeneratorOpenLog.OL_EntryToWrite oletw;
			try {
				oletw = LogGeneratorOpenLog._olQueue.take();
				if (oletw != null)
					oletw._logGenerator._olWriter.writeLogRecToDB(Factory.getSession(SessionType.CURRENT), oletw._logRec,
							oletw._logGenerator._startTime);
			} catch (InterruptedException e) {
				System.out.println("LogTaskletOpenLog: Caught an InterruptedException; finishing ...");
				break;
			} catch (Throwable t) {
				System.err.println("LogTaskletOpenLog: Caught an unexpected exception " + t.getClass().getName() + ":");
				t.printStackTrace();
				System.err.println("LogTaskletOpenLog: Aborting.");
				break;
			}
		}
		LogGeneratorOpenLog._olQueue = null;
	}

}
