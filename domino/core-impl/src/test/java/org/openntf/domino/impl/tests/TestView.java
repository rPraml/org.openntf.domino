package org.openntf.domino.impl.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openntf.domino.Database;
import org.openntf.domino.View;
import org.openntf.domino.ViewEntry;
import org.openntf.domino.ext.Session;
import org.openntf.domino.junit.DominoJUnitRunner;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

@RunWith(DominoJUnitRunner.class)
public class TestView {

	@Test
	public void testViewIteration() {
		Session s = Factory.getSession(SessionType.CURRENT);
		Database db = s.getDatabase("names.nsf");
		View view = db.getView("($VIMPeopleAndGroups)");

		for (ViewEntry entry : view.getAllEntries()) {
			String form = entry.getDocument().getItemValueString("Form");
			System.out.println(form);
		}

	}

}
