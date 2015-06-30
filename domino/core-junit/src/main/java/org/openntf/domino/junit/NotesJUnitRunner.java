package org.openntf.domino.junit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import lotus.domino.NotesException;
import lotus.domino.NotesThread;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
//import org.openntf.domino.WrapperFactory;
//import org.openntf.domino.utils.Factory;

public class NotesJUnitRunner extends AbstractJUnitRunner {
	private lotus.domino.Session mastersession;

	public NotesJUnitRunner(final Class<?> testClass) throws InitializationError {
		super(testClass);
	}

	@Override
	protected void startUp() {
		NotesThread.sinitThread();
		try {
			// keep alive one session over all test cases - this will improve execution speed
			mastersession = lotus.domino.NotesFactory.createSession();
			TestEnv.setup(mastersession);
		} catch (NotesException ne) {
			ne.printStackTrace();
		}

	}

	@Override
	protected void tearDown() {
		try {
			if (mastersession != null)
				mastersession.recycle();
		} catch (NotesException e) {
			e.printStackTrace();
		}
		NotesThread.stermThread();
	}

	@Override
	protected void beforeTest(final FrameworkMethod method) {
		try {
			String runAs = getRunAs(method);
			if (runAs == null) {
				TestEnv.session = lotus.domino.local.Session.createSession();
			} else {
				TestEnv.session = lotus.domino.local.Session.createSessionWithTokenEx(runAs);
			}
			assertNotNull(TestEnv.session);
			String db = getDatabase(method);
			if (db != null) {
				String server = "";
				String dbpath = db;
				int sep;
				if ((sep = db.indexOf("!!")) > -1) {
					server = db.substring(0, sep);
					dbpath = db.substring(sep + 2);
				}
				TestEnv.database = TestEnv.session.getDatabase(server, dbpath);
				assertNotNull(TestEnv.database);
			}

		} catch (NotesException ne) {
			ne.printStackTrace();
			fail(ne.toString());
		}
	}

	@Override
	protected void afterTest(final FrameworkMethod method) {
		// TODO Auto-generated method stub
		try {
			if (TestEnv.database != null)
				TestEnv.database.recycle();
			if (TestEnv.session != null)
				TestEnv.session.recycle();
		} catch (NotesException e) {
			e.printStackTrace();
		}
		TestEnv.session = null;
		TestEnv.database = null;
	}

}
