package org.openntf.domino.tests;

import java.io.PrintStream;
import java.lang.reflect.Method;

import org.openntf.domino.Session;
import org.openntf.domino.commons.IName;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

// @RunWith(DominoJUnitRunner.class)
public class NameTest82x {

	private static String[] _getMeths = new String[] { "Addr821", "Addr822LocalPart", "Addr822Phrase", //
		"Addr822Comment1", "Addr822Comment2", "Addr822Comment3", "Canonical", "Abbreviated", //
		"Common", "Surname", "Given", "Country", "OrgUnit1", "OrgUnit2", "ADMD", "PRMD", //
		"Organization", "Initials", "Keyword" };

	private PrintStream _ps;

	protected void testOutputNames(final String inputName, final lotus.domino.Name lotusName, final IName odaName, final IName odaNameIndep)
			throws Exception {
		_ps.println("=============================================================");
		_ps.println("Input='" + inputName + "'");
		for (String getMeth : _getMeths)
			testOutput1Part(lotusName, odaName, odaNameIndep, "get" + getMeth, false);
		testOutput1Part(lotusName, odaName, odaNameIndep, "getRFC82xInternetAddress", true);
		testOutput1Part(lotusName, odaName, odaNameIndep, "getIDprefix", true);
		testOutput1Part(lotusName, odaName, odaNameIndep, "getNameFormat", true);
		testOutput1Part(lotusName, odaName, odaNameIndep, "getNameError", true);
	}

	private static Object[] _emptyArgs = new Object[0];

	private void testOutput1Part(final lotus.domino.Name lotusName, final IName odaName, final IName odaNameIndep,
			final String getPartMethod, final boolean onlyODA) throws Exception {
		_ps.println("    --------------------------------------------------------");
		if (!onlyODA) {
			Method lotus = lotusName.getClass().getDeclaredMethod(getPartMethod);
			String lotusRes = (String) lotus.invoke(lotusName, _emptyArgs);
			_ps.println("    Lotus-" + getPartMethod + "='" + lotusRes + "'");
		}
		Method oda = odaName.getClass().getDeclaredMethod(getPartMethod);
		String odaRes = oda.invoke(odaName, _emptyArgs).toString();
		_ps.println("    OPNTF-" + getPartMethod + "='" + odaRes + "'");
		Method odaIndep = odaNameIndep.getClass().getDeclaredMethod(getPartMethod);
		String odaResIndep = odaIndep.invoke(odaNameIndep, _emptyArgs).toString();
		_ps.println("    Indep-" + getPartMethod + "='" + odaResIndep + "'");
	}

	@Deprecated
	protected IName testName(final String what) throws Exception {
		Session sess = Factory.getSession(SessionType.CURRENT);
		lotus.domino.Session lotusSess = sess.getFactory().toLotus(sess);
		org.openntf.domino.Name odaName = sess.createName(what);
		IName odaNameIndep = IName.PROTOTYPE.create(what);
		lotus.domino.Name lotusName = lotusSess.createName(what);
		testOutputNames(what, lotusName, odaName, odaNameIndep);
		return odaName;
	}

	//	@Test
	public void run() throws Exception {
		_ps = System.out;
		_ps = new PrintStream("c:/stein/misc/work/nametest.txt");
		testName("Jukka Määttä <jkorpela@cc.hut.fi> (Jukka) (Korpela)");
		//		if (true)
		//			return;
		//		testName("                           ");
		//		testName("X");
		//		testName("jkorpela@cc.hut.fi");
		//		testName("jkorpela@cc.hut.fi (Jukka Korpela)");
		testName("Jukka Korpela <jkorpela@cc.hut.fi> (Jukka) (Korpela)");
		//		testName("stein.im.ried@gmail.com (  Manfred Steinsiek  )");
		testName("Jukka Korpela jkorpela@cc.hut.fi (Jukka) (Korpela)");
		testName("CN=Roland Praml/OU=01/OU=int/O=FOCONIS");
		testName("CN=Roland Praml/OU=01/OU=int/O=FOCONIS");
		testName("cn=Roland Praml/o=foconis/c=DE");
		testName("Roland Praml/foconis/DE");
		//		testName("Roland Praml/foconis/DE@FOCONIS");
		//		testName("cn=Roland Praml/o=foconis/c=DE@FOCONIS");
		//		testName("cn=*/o=foconis/c=DE");
		//		testName("*/entw/technik/foconis/et");
		//		testName("*@foconis.de");
		//		testName("Jukka Korpela <jkorpela@cc.hut.fi> (Jukka) Korpela) (Fehler");
		//		testName("CN=Fred Feuerstein");
		//		testName("CN=Fred Feuerstein/A=Unknown/Q=My Generation");
		//		testName("CN=Fred Feuerstein/A=Unknown/Q=My Generation");
		//		testName("CN=Fred Feuerstein/I=My@Generation/C=DE");
		//		testName("CN=Fred Feuerstein/I=My@Generation/C=DE");
		//		testName("Fred @ Feuer/stein <ffs@Generation.DE>");
		//		testName("CN=Fred Feuerstein/ou=01");
		//		testName("CN=Fred Feuerstein/ou=QS/o=Foconis/C=Deutschland (West)");
		//		testName("CN=Fred Feuerstein/I=FFS/Q=My Generation/P=Privat");
		//		testName("Fred Feuerstein/FFS/My Generation/Pt Privat/ET");
		//		testName("Fred Feuerstein/FFS/My Generation/Pt Privat/ETX");
		//		testName("Fred Feuerstein");
		testName("S=Feuerstein/A=Unknown/G=Fred/Q=My Generation/CN=Fred Feuerstein");
		testName("Deng Hsiao Ping <denghp@central.gov.zh> (Deng) (Hsiao)(    ) (   Ping   )");
		testName("Fred Feuerstein/QS/Foconis/Deutschland (West)");
		//		testName("Fred Feuerstein/QS/Foconis/DE");
		//		testName("Fred Feuerstein/QS/Foconis/DEU");
		//		testName("Fred Feuerstein/QS/Foconis/XY");
		//		testName("Fred Feuerstein/QS/Foconis/12");

		//		testName("CN=Roland Praml/OU4=01/OU1=int/ou3=Entw/O=FOCONIS");
		//		String[] cs = Locale.getISOCountries();
		//		for (String c : cs)
		//			_ps.println(c);

		//		testName("Roland Praml/01/int/Entw/FOCONIS");
		//		testName("Roland Praml/foconis/DE@FOCONIS@GAD");
		//		testName("cn=Roland Praml/o=foconis/c=DE@FOCONIS@GAD");
		//		testName("cn=Roland Praml/X=a/ZZZ=b");

		//		testName("Mann Stein/dd/dev/xxx/hr/Foc");
		//		testName("Mann Stein/dd/dev/xxx/hr/Foc/de");
		//		testName("Mann Stein/dd/dev/xxx/hr/Foc/deu");
		//				testName("Phrase  <John Smith/Dev/Company/EN%Company@Company.en>");
		//		testName("\"Phrase with <>\" <    JohnSmith@Company.en    > ((Works?\\\\) too?)");
		//		testName("\"Phrase with <>\" <    JohnSmith@Company.en    > ((Works?) too?))");
		testName("\"Phrase with <>\" <    JohnSmith@Company.en    > (Shouldn't work\\)");
		testName("\"Phrase with <>\" <    JohnSmith@Company.en    > (Should work\\\\)");
		testName("\"Phrase with <>\" <    IA=!#$%&*+-/=?^_`{}|~@example.org    > (Shouldn't work)");
		//		testName("Q=Manni/CN=*");
		//		testName("Ich/DE");
		//		testName("cn=Mann Stein/O=Foc/ou2=xxx/ou=dev/ou4=dd/ou=hr");
		//		testName("cn= Mann Stein/O= Foc/ou2=xxx/ou=dev/ou4=dd/ou=hr");
		//		testName("cn= Mann Stein/O= Foc/ou=xxx/ou=dev/ou=dd/ou=hr");
		//		testName("cn=John Smith/O=Comp/ou2=xxx/ou3=dev/ou4=dd/ou1=hr");
	}

	//	public static void main(final String[] args) throws Exception {
	//		NotesThread.sinitThread();
	//		try {
	//
	//			LifeCycleManager.startup();
	//
	//			LifeCycleManager.beforeRequest(Factory.STRICT_THREAD_CONFIG);
	//			new NameTest82x().testRFC82x();
	//			LifeCycleManager.afterRequest();
	//			LifeCycleManager.shutdown();
	//		} finally {
	//			NotesThread.stermThread();
	//		}
	//	}

}
