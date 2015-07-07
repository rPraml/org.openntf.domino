package org.openntf.domino.commons.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openntf.domino.commons.IName;
import org.openntf.domino.commons.Names;

public class TestNames {

	@Before
	public void setup() {
		IName.PROTOTYPE.setLocalServerName("CN=dummy/O=ci-test");
	}

	@Test
	public void testParse() {
		IName name = IName.PROTOTYPE.create("CN=Roland Praml/OU=01/OU=int/O=FOCONIS");
		assertEquals("Roland Praml", name.getCommon());
		assertEquals("int", name.getOrgUnit1());
		assertEquals("01", name.getOrgUnit2());
		assertEquals("FOCONIS", name.getOrganization());

		assertEquals("Roland Praml/01/int/FOCONIS", name.getAbbreviated());

	}

	@Test
	public void testFormatAsRole() {
		assertEquals("[role]", Names.formatAsRole("role"));
		assertEquals("[role]", Names.formatAsRole("[role]"));
		assertEquals("", Names.formatAsRole(""));
	}

	//	@Test
	//	public void testGetNamePart() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testGetNameParts() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testGetAbbreviatedStringArray() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testGetAbbreviatedString() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testGetCanonicalStringArray() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testGetCanonicalString() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testGetCommonStringArray() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testGetCommonString() {
	//		fail("Not yet implemented");
	//	}

}
