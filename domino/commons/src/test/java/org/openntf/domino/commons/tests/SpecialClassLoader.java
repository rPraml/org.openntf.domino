package org.openntf.domino.commons.tests;

import javax.xml.bind.DatatypeConverter;

/**
 * Classloader for {@link TestThreadUtils}
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public class SpecialClassLoader extends ClassLoader {
	/**
	 * The Bytecode represents the following class
	 * 
	 * <pre>
	 * 
	 * import java.io.Serializable;
	 * 
	 * public class ByteCodeClass implements Serializable {
	 * 	private static final long serialVersionUID = 1L;
	 * 	private String value;
	 * 
	 * 	public ByteCodeClass() {
	 * 
	 * 	}
	 * 
	 * 	public ByteCodeClass(final String value) {
	 * 		this.value = value;
	 * 	}
	 * 
	 * 	&#064;Override
	 * 	public String toString() {
	 * 		return value;
	 * 	}
	 * 
	 * }
	 * </pre>
	 */
	public static final byte[] bytecode = DatatypeConverter
			.parseBase64Binary("yv66vgAAADIAHgcAAgEADUJ5dGVDb2RlQ2xhc3MHAAQBABBqYXZhL2xhbmcvT2JqZWN0BwAGAQAU"
					+ "amF2YS9pby9TZXJpYWxpemFibGUBABBzZXJpYWxWZXJzaW9uVUlEAQABSgEADUNvbnN0YW50VmFs"
					+ "dWUFAAAAAAAAAAEBAAV2YWx1ZQEAEkxqYXZhL2xhbmcvU3RyaW5nOwEABjxpbml0PgEAAygpVgEA"
					+ "BENvZGUKAAMAEgwADgAPAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAE"
					+ "dGhpcwEAD0xCeXRlQ29kZUNsYXNzOwEAFShMamF2YS9sYW5nL1N0cmluZzspVgkAAQAZDAAMAA0B"
					+ "AAh0b1N0cmluZwEAFCgpTGphdmEvbGFuZy9TdHJpbmc7AQAKU291cmNlRmlsZQEAEkJ5dGVDb2Rl"
					+ "Q2xhc3MuamF2YQAhAAEAAwABAAUAAgAaAAcACAABAAkAAAACAAoAAgAMAA0AAAADAAEADgAPAAEA"
					+ "EAAAADMAAQABAAAABSq3ABGxAAAAAgATAAAACgACAAAABwAEAAkAFAAAAAwAAQAAAAUAFQAWAAAA"
					+ "AQAOABcAAQAQAAAARgACAAIAAAAKKrcAESortQAYsQAAAAIAEwAAAA4AAwAAAAsABAAMAAkADQAU"
					+ "AAAAFgACAAAACgAVABYAAAAAAAoADAANAAEAAQAaABsAAQAQAAAALwABAAEAAAAFKrQAGLAAAAAC"
					+ "ABMAAAAGAAEAAAARABQAAAAMAAEAAAAFABUAFgAAAAEAHAAAAAIAHQ==");

	public SpecialClassLoader(final ClassLoader parent) {
		super(parent);
	}

	@Override
	protected synchronized Class<?> loadClass(final String className, final boolean resolveClass) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		if ("ByteCodeClass".equals(className)) {
			return defineClass(className, bytecode, 0, bytecode.length);
		}
		return super.loadClass(className, resolveClass);
	}
}