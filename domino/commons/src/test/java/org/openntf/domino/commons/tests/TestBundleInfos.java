package org.openntf.domino.commons.tests;

import org.junit.Test;
import org.openntf.domino.commons.utils.BundleInfos;

public class TestBundleInfos {
	@Test
	public void test1() {
		BundleInfos bi = BundleInfos.getInstance(BundleInfos.class);
		//assertEquals("OpenNTF Domino API Common classes", bi.getBundleName());
	}
}
