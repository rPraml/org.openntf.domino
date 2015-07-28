/*
 * Copyright 2015 - FOCONIS AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express o 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 */
package org.openntf.domino.commons;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to compute several hashes. (mainly MD5, maybe extended to SHA-1 and others)
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public enum Hash {
	;

	/**
	 * Checksum (various variants). The algorithm to be used has to be passed as parameter.
	 */
	public static String checksum(final byte[] whose, final String algorithmName) throws NoSuchAlgorithmException {
		MessageDigest algorithm = MessageDigest.getInstance(algorithmName);
		algorithm.reset();
		algorithm.update(whose);
		return finishChecksum(algorithm);
	}

	private static String finishChecksum(final MessageDigest algorithm) {
		BigInteger bi = new BigInteger(1, algorithm.digest());
		return bi.toString(16);
	}

	public static String checksum(final String whose, final String alg) throws NoSuchAlgorithmException {
		return checksum(whose.getBytes(Strings.UTF_8_CHARSET), alg);
	}

	public static String checksum(final File whose, final String alg) throws IOException, NoSuchAlgorithmException {
		byte[] buffer = new byte[32768];
		MessageDigest algorithm = MessageDigest.getInstance(alg);
		algorithm.reset();
		FileInputStream fis = new FileInputStream(whose);
		int nread;
		try {
			while ((nread = fis.read(buffer)) > 0)
				algorithm.update(buffer, 0, nread);
			return finishChecksum(algorithm);
		} finally {
			fis.close();
		}
	}

	public static String checksum(final Serializable whose, final String algorithm) throws NoSuchAlgorithmException {
		String result = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(whose);
			result = checksum(baos.toByteArray(), algorithm);
			out.close();
		} catch (IOException ioex) {
			throw new RuntimeException("Unexpected IOException", ioex);
		}
		return result;
	}

	/**
	 * Same variants for MD5.
	 */
	public static String md5(final byte[] whose) {
		try {
			return checksum(whose, "MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm present, why that?");
		}
	}

	public static String md5(final String whose) {
		try {
			return checksum(whose, "MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm present, why that?");
		}
	}

	public static String md5(final File whose) throws IOException {
		try {
			return checksum(whose, "MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm present, why that?");
		}
	}

	public static String md5(final Serializable whose) {
		try {
			return checksum(whose, "MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm present, why that?");
		}
	}
}
