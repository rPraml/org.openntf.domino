package org.openntf.domino.commons.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openntf.domino.commons.utils.ThreadUtils;
import org.openntf.domino.commons.utils.ThreadUtils.LoaderObjectInputStream;

@RunWith(Parameterized.class)
public class TestThreadUtils {

	@Parameterized.Parameters
	public static Collection primeNumbers() {
		return Arrays.asList(new Object[][] { { false }, { true } });
	}

	private SecurityManager oldSM;
	private SecurityManager newSM;

	public TestThreadUtils(final boolean useSecurityManager) {
		if (useSecurityManager) {
			newSM = new SecurityManager() {
				@Override
				public void checkPermission(final Permission paramPermission) {

				}

				@Override
				public void checkPermission(final Permission paramPermission, final Object paramObject) {
				}
			};
		}
	}

	@Before
	public void initialize() {
		oldSM = System.getSecurityManager();
		System.setSecurityManager(newSM);
	}

	@After
	public void terminate() {
		System.setSecurityManager(oldSM);
	}

	// ----------- start of tests
	@Test
	public void testGetContextClassLoader() {
		ClassLoader cl = ThreadUtils.getContextClassLoader();
		assertEquals(Thread.currentThread().getContextClassLoader(), cl);
	}

	@Test
	public void testSetContextClassLoader() throws ClassNotFoundException {
		ClassLoader oldCL = ThreadUtils.getContextClassLoader();
		try {
			ThreadUtils.setContextClassLoader(new SpecialClassLoader(oldCL));
			Class c = ThreadUtils.getClass("ByteCodeClass");
			assertEquals("ByteCodeClass", c.getName());
		} finally {
			ThreadUtils.setContextClassLoader(oldCL);
		}
	}

	@Test(expected = ClassNotFoundException.class)
	public void testGetClass() throws ClassNotFoundException {
		ClassLoader oldCL = ThreadUtils.getContextClassLoader();
		try {
			ThreadUtils.setContextClassLoader(new SpecialClassLoader(oldCL));
			Class c = ThreadUtils.getClass("DoesReallyNotExist");
		} finally {
			ThreadUtils.setContextClassLoader(oldCL);
		}
	}

	@Test(expected = ClassNotFoundException.class)
	public void testLoaderObjectInputStream() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {

		ClassLoader cl = new SpecialClassLoader(getClass().getClassLoader());
		Class<?> cls = cl.loadClass("ByteCodeClass");
		Constructor<?> cTor = cls.getConstructor(String.class);
		Object instance = cTor.newInstance("Hello World!");
		assertEquals("Hello World!", instance.toString());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(instance);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new LoaderObjectInputStream(bais);
		Object instance2 = ois.readObject();

		assertEquals("Hello World!", instance2.toString());
		assertFalse(instance == instance2);

	}

	public void testLoaderObjectInputStream2() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {

		ClassLoader cl = new SpecialClassLoader(getClass().getClassLoader());
		Class<?> cls = cl.loadClass("ByteCodeClass");
		Constructor<?> cTor = cls.getConstructor(String.class);
		Object instance = cTor.newInstance("Hello World!");
		assertEquals("Hello World!", instance.toString());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(instance);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new LoaderObjectInputStream(bais);

		// try again with switched classloader
		ClassLoader oldCL = ThreadUtils.getContextClassLoader();
		try {
			ThreadUtils.setContextClassLoader(new SpecialClassLoader(oldCL));
			Object instance2 = ois.readObject();

			assertEquals("Hello World!", instance2.toString());
			assertFalse(instance == instance2);
		} finally {
			ThreadUtils.setContextClassLoader(oldCL);
		}

	}

}
