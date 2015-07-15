package org.openntf.domino.junit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.logging.LogManager;

import lotus.domino.NotesException;
import lotus.domino.NotesThread;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openntf.domino.WrapperFactory;
import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.LifeCycleManager;
import org.openntf.domino.session.NativeSessionFactory;
import org.openntf.domino.session.SessionFullAccessFactory;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

public class DominoTestCase {
	private static lotus.domino.Session mastersession;

	@BeforeClass
	public static void _beforeClass() {
		NotesThread.sinitThread();
		try {
			// keep alive one session over all test cases - this will improve execution speed
			mastersession = lotus.domino.NotesFactory.createSession();
			TestEnv.setup(mastersession);
		} catch (NotesException ne) {
			ne.printStackTrace();
		}
		LogManager.getLogManager().reset();
		LifeCycleManager.startup();
	}

	@AfterClass
	public static void _afterClass() {
		try {
			if (mastersession != null)
				mastersession.recycle();
		} catch (NotesException e) {
			e.printStackTrace();
		}

		LifeCycleManager.shutdown();
		NotesThread.stermThread();
	}

	@Before
	public void _before() {
		try {
			IRequest request = new JUnitRequest("JUnit: " + getClass().getName(), null);
			LifeCycleManager.beforeRequest(request);
			String db = "names.nsf";
			Factory.setSessionFactory(new NativeSessionFactory(db), SessionType.CURRENT);
			Factory.setSessionFactory(new SessionFullAccessFactory(db), SessionType.CURRENT_FULL_ACCESS);

			TestEnv.session = Factory.getSession(SessionType.CURRENT);
			TestEnv.database = TestEnv.session.getCurrentDatabase();
			if (db != null) {
				assertNotNull(TestEnv.database);
			}

		} catch (NotesException ne) {
			ne.printStackTrace();
			fail(ne.toString());
		}
	}

	@After
	public void _after() {
		WrapperFactory wf = Factory.getWrapperFactory();
		wf.recycle(TestEnv.database);
		wf.recycle(TestEnv.session);
		TestEnv.session = null;
		TestEnv.database = null;
		LifeCycleManager.afterRequest();
	}

}
