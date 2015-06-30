package org.openntf.domino.commons;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	 * Checksum.
	 * 
	 * @param bytes
	 *            the bytes
	 * @param alg
	 *            the alg
	 * @return the string
	 * @throws NoSuchAlgorithmException
	 */
	public static String checksum(final byte[] bytes, final String alg) throws NoSuchAlgorithmException {
		byte[] defaultBytes = bytes;

		MessageDigest algorithm = MessageDigest.getInstance(alg);
		algorithm.reset();
		algorithm.update(defaultBytes);
		byte[] messageDigest = algorithm.digest();
		BigInteger bi = new BigInteger(1, messageDigest);

		return bi.toString(16);
	}

	/**
	 * Checksum.
	 * 
	 * @param bytes
	 *            the bytes
	 * @param alg
	 *            the alg
	 * @return the string
	 * @throws NoSuchAlgorithmException
	 */
	public static String checksum(final String payload, final String alg) throws NoSuchAlgorithmException {
		byte[] defaultBytes = payload.getBytes(Strings.UTF_8_CHARSET);

		MessageDigest algorithm = MessageDigest.getInstance(alg);
		algorithm.reset();
		algorithm.update(defaultBytes);
		byte[] messageDigest = algorithm.digest();
		BigInteger bi = new BigInteger(1, messageDigest);

		return bi.toString(16);
	}

	/**
	 * Checksum.
	 * 
	 * @param bytes
	 *            the bytes
	 * @param alg
	 *            the alg
	 * @return the string
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	public static String checksum(final File file, final String alg) throws IOException, NoSuchAlgorithmException {
		byte[] dataBytes = new byte[4096];

		FileInputStream fis = new FileInputStream(file);

		try {
			MessageDigest algorithm = MessageDigest.getInstance(alg);
			algorithm.reset();
			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
				algorithm.update(dataBytes, 0, nread);
			}
			byte[] messageDigest = algorithm.digest();
			BigInteger bi = new BigInteger(1, messageDigest);

			return bi.toString(16);
		} finally {
			fis.close();
		}
	}

	/**
	 * Checksum.
	 * 
	 * @param object
	 *            the object
	 * @param algorithm
	 *            the algorithm
	 * @return the string
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static String checksum(final Serializable object, final String algorithm) throws NoSuchAlgorithmException {
		String result = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(object);
			result = checksum(baos.toByteArray(), algorithm);
			out.close();
		} catch (IOException ioex) {
			throw new RuntimeException("Unexpected IOException", ioex);
		}
		return result;
	}

	/**
	 * Md5.
	 * 
	 * @param bytes
	 *            the byte array
	 * @return the string representing the MD5 hash value of the byte array
	 */
	public static String md5(final byte[] bytes) {
		try {
			return checksum(bytes, "MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm present, why that?");
		}
	}

	/**
	 * Md5.
	 * 
	 * @param file
	 *            the file
	 * @return the string representing the MD5 hash value of the file
	 */
	public static String md5(final File file) throws IOException {
		try {
			return checksum(file, "MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm present, why that?");
		}
	}

	/**
	 * Md5.
	 * 
	 * @param object
	 *            the Serializable object
	 * @return the string representing the MD5 hash value of the serialized version of the object
	 * @throws IOException
	 */
	public static String md5(final Serializable object) {
		try {
			return checksum(object, "MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm present, why that?");
		}
	}

	/**
	 * Md5.
	 * 
	 * @param object
	 *            the Serializable object
	 * @return the string representing the MD5 hash value of the serialized version of the object
	 * @throws IOException
	 */
	public static String md5(final String s) {
		try {
			return checksum(s, "MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm present, why that?");
		}
	}
}
