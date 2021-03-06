/*
 * Copyright 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package org.openntf.domino.thread;

import java.util.logging.Logger;

import lotus.domino.NotesThread;

import org.openntf.domino.commons.ILifeCycle;
import org.openntf.domino.commons.IO;
import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.LifeCycleManager;
import org.openntf.domino.session.ISessionFactory;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.utils.ODAUtils;

/**
 * The Class DominoThread extends the NotesThread and clones the current SessionFactory.
 */
public class DominoThread extends NotesThread implements ILifeCycle {
	private static final Logger log_ = Logger.getLogger(DominoThread.class.getName());
	private transient Runnable runnable_;
	protected int nativeId_;
	private final ISessionFactory sessionFactory_;
	private final IRequest request_;

	private static IRequest getRequest(final String action) {
		IRequest request = LifeCycleManager.getCurrentRequest();
		if (request == null) {
			return new DominoRequest(action).threadConfig(Factory.STRICT_THREAD_CONFIG);
		} else {
			return request.clone(action);
		}
	}

	/**
	 * Instantiates a new domino thread.
	 */
	public DominoThread() {
		sessionFactory_ = Factory.getSessionFactory(SessionType.CURRENT);
		request_ = getRequest("DominoThread: " + getClass().getName());
	}

	/**
	 * Instantiates a new domino thread.
	 * 
	 * @param threadName
	 *            the thread name
	 */
	public DominoThread(final String threadName) {
		super(threadName);
		sessionFactory_ = Factory.getSessionFactory(SessionType.CURRENT);
		request_ = getRequest("DominoThread: " + threadName + " (" + getClass().getName() + ")");

	}

	/**
	 * Instantiates a new domino thread.
	 * 
	 * @param runnable
	 *            the runnable
	 */
	public DominoThread(final Runnable runnable) {
		super();
		runnable_ = runnable;
		sessionFactory_ = Factory.getSessionFactory(SessionType.CURRENT);
		request_ = getRequest("DominoThread: " + runnable.getClass().getName());
	}

	/**
	 * Instantiates a new domino thread.
	 * 
	 * @param runnable
	 *            the runnable
	 * @param threadName
	 *            the thread name
	 */
	public DominoThread(final Runnable runnable, final String threadName) {
		super(threadName);
		runnable_ = runnable;
		sessionFactory_ = Factory.getSessionFactory(SessionType.CURRENT);
		request_ = getRequest("DominoThread: " + threadName + " (" + runnable.getClass().getName() + ")");
	}

	/**
	 * Instantiates a new domino thread.
	 * 
	 * @param runnable
	 *            the runnable
	 * @param threadName
	 *            the thread name
	 * @param sessionFactory
	 *            the session factory
	 */
	public DominoThread(final Runnable runnable, final String threadName, final ISessionFactory sessionFactory) {
		super(threadName);
		runnable_ = runnable;
		sessionFactory_ = sessionFactory;
		request_ = getRequest("DominoThread: " + threadName + " (" + runnable.getClass().getName() + ")");
	}

	/**
	 * Instantiates a new domino thread.
	 * 
	 * @param threadName
	 *            the thread name
	 * @param sessionFactory
	 *            the session factory
	 */
	public DominoThread(final String threadName, final ISessionFactory sessionFactory) {
		super(threadName);
		sessionFactory_ = sessionFactory;
		request_ = getRequest("DominoThread: " + threadName + " (" + getClass().getName() + ")");
	}

	/**
	 * Instantiates a new domino thread.
	 * 
	 * @param runnable
	 *            the runnable
	 * @param threadName
	 *            the thread name
	 * @param sessionFactory
	 *            the session factory
	 */
	public DominoThread(final ISessionFactory sessionFactory) {
		super();
		sessionFactory_ = sessionFactory;
		request_ = getRequest("DominoThread: " + getClass().getName());
	}

	/**
	 * Instantiates a new domino thread.
	 * 
	 * @param runnable
	 *            the runnable
	 * @param threadName
	 *            the thread name
	 * @param sessionFactory
	 *            the session factory
	 */
	public DominoThread(final Runnable runnable, final ISessionFactory sessionFactory) {
		super();
		runnable_ = runnable;
		sessionFactory_ = sessionFactory;
		request_ = getRequest("DominoThread: " + runnable.getClass().getName());
	}

	public Runnable getRunnable() {
		return runnable_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void runNotes() {
		try {
			getRunnable().run();	//NTF: we're already inside the NotesThread.run();
			//Note: if the runnable is a ThreadPoolExecutor.Worker, then this process will not end
			//until the Executor is shutdown or the keep alive expires.
		} catch (Throwable t) {
			t.printStackTrace();
			ODAUtils.handleException(t);
		}
	}

	@Override
	public void initThread() {
		super.initThread();
		nativeId_ = this.getNativeThreadID();
		log_.fine("DEBUG: Initializing a " + toString());
		LifeCycleManager.beforeRequest(request_);
		Factory.setSessionFactory(sessionFactory_, SessionType.CURRENT);
		LifeCycleManager.addLifeCycle(this);
	}

	@Override
	public void termThread() {
		log_.fine("DEBUG: Terminating a " + toString());
		LifeCycleManager.removeLifeCycle(this);
		LifeCycleManager.afterRequest();
		super.termThread();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		if (nativeId_ != 0) {
			sb.append(String.format(" [%04x]", nativeId_));
		}
		sb.append(": ");
		if (getRunnable() != null) {
			sb.append(getRunnable().getClass().getName());
		} else {
			sb.append(getClass().getName());
		}
		return sb.toString();
	}

	/**
	 * Method to run a standalone app in mainThread
	 * 
	 * @param app
	 *            the app to run
	 * @param sf
	 *            the sessionFactory
	 */
	public static void runApp(final Runnable app, final ISessionFactory sf) {
		LifeCycleManager.startup();
		lotus.domino.NotesThread.sinitThread(); // we must keep one thread open
		IRequest request = new DominoRequest("app: " + app.getClass().getName()).threadConfig(Factory.STRICT_THREAD_CONFIG);
		LifeCycleManager.beforeRequest(request);
		Factory.setSessionFactory(sf, SessionType.CURRENT);

		try {
			app.run();
		} finally {
			LifeCycleManager.afterRequest();
			lotus.domino.NotesThread.stermThread();
			LifeCycleManager.shutdown();
		}
	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		IO.println(DominoThread.this, "Shutdown " + DominoThread.this);
		DominoThread.this.interrupt();
		try {
			DominoThread.this.join(30 * 1000); // give the thread 30 seconds to join after interrupt.
		} catch (InterruptedException e) {
		}
		if (DominoThread.this.isAlive()) {
			IO.println(DominoThread.this, "WARNING " + DominoThread.this + " is still alive after 30 secs. Continuing anyway.");
		}

	}
}
