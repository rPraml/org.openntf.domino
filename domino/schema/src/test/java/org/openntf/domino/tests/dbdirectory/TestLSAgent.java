package org.openntf.domino.tests.dbdirectory;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openntf.domino.Agent;
import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.Session;
import org.openntf.domino.junit.DominoJUnitRunner;
import org.openntf.domino.utils.Factory;

@RunWith(DominoJUnitRunner.class)
public class TestLSAgent {

	@Test
	public void test() throws IOException {
		Session session = Factory.getNamedSession("CN=junit/O=CI-DUMMY", false);
		Database db = session.getDatabase("citest.nsf");

		Agent agent = db.getAgent("LS-Agent");
		Document doc = db.createDocument();
		agent.runWithDocumentContext(doc);
		assertEquals("This value comes from LotusScript", doc.getItemValueString("Return"));
	}

}
