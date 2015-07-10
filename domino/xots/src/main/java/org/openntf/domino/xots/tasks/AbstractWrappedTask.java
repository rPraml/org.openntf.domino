package org.openntf.domino.xots.tasks;

import java.lang.reflect.Constructor;
import java.util.Observable;
import java.util.Observer;

import org.openntf.domino.commons.IRequest;
import org.openntf.domino.xots.Tasklet;

/**
 * A Wrapper for callables and runnable that provides proper setup/teardown
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public abstract class AbstractWrappedTask implements IWrappedTask {

	protected Object wrappedTask;

	protected Tasklet.Scope scope;
	protected Tasklet.Context context;
	protected IRequest taskRequest;

	/**
	 * Sets the wrapped Task.
	 * 
	 * @param task
	 *            the runnable
	 */
	protected synchronized void setWrappedTask(final Object task) {
		throw new AbstractMethodError();
	}

	/**
	 * Returns the wrapped task
	 */
	@Override
	public synchronized Object getWrappedTask() {
		return wrappedTask;
	}

	/**
	 * adds an observer to the wrapped task
	 */
	@Override
	public void addObserver(final Observer o) {
		Object task = getWrappedTask();
		if (task instanceof Observable) {
			((Observable) task).addObserver(o);
		}
	}

	/**
	 * stops the wrapped task if it does implement {@link Tasklet.Interface}
	 */
	@Override
	public void stop() {
		Object task = getWrappedTask();
		if (task instanceof Tasklet.Interface) {
			((Tasklet.Interface) task).stop();
		}
	}

	@Override
	public String getDescription() {
		Object task = getWrappedTask();
		if (task instanceof Tasklet.Interface) {
			return ((Tasklet.Interface) task).getDescription();
		}
		return task.getClass().getSimpleName();
	}

	/**
	 * Common method that does setUp/tearDown before executing the wrapped object
	 * 
	 * @param callable
	 * @param runnable
	 * @return
	 * @throws Exception
	 */
	protected abstract Object callOrRun() throws Exception;

	protected abstract Object invokeWrappedTask() throws Exception;

	/**
	 * Finds the constructor for the given Tasklet
	 * 
	 * @param clazz
	 * @param args
	 * @return
	 */
	protected Constructor<?> findConstructor(final Class<?> clazz, final Object[] args) {
		// sanity check if this is a public tasklet
		Tasklet annot = clazz.getAnnotation(Tasklet.class);
		if (annot == null) {
			throw new IllegalStateException("Cannot run " + clazz.getName() + ", because it does not annotate @Tasklet.");
		}

		//		if (!(Callable.class.isAssignableFrom(clazz)) && !(Runnable.class.isAssignableFrom(clazz))) {
		//			throw new IllegalStateException("Cannot run " + clazz.getName() + ", because it is no Runnable or Callable.");
		//		}

		// find the constructor
		Class<?> ctorClasses[] = new Class<?>[args.length];
		Object ctorArgs[] = new Object[args.length];
		for (int i = 0; i < ctorClasses.length; i++) {
			Object arg;
			ctorArgs[i] = arg = args[i];
			ctorClasses[i] = arg == null ? null : arg.getClass();
		}

		Constructor<?> cTor = null;
		try {
			cTor = clazz.getConstructor(ctorClasses);
		} catch (NoSuchMethodException nsme1) {
			try {
				cTor = clazz.getConstructor(new Class<?>[] { Object[].class });
				ctorArgs = new Object[] { ctorArgs };
			} catch (NoSuchMethodException nsme2) {

			}
		}
		if (cTor == null) {
			throw new IllegalStateException("Cannot run " + clazz.getName() + ", because it has no constructor for Arguments: " + ctorArgs);
		}
		return cTor;
	}

}