package org.openntf.domino.commons.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openntf.domino.commons.Hash;

public class TestHash {

	@Test
	public void testString() {
		String s = "Hello World!";
		byte[] b = new byte[] { 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x57, 0x6f, 0x72, 0x6c, 0x64, 0x21 };
		assertEquals("ed076287532e86365e841e92bfc50d8c", Hash.md5(s));
		assertEquals("ed076287532e86365e841e92bfc50d8c", Hash.md5(b));

	}
}
