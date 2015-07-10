package org.openntf.domino.xots.tasks;

import org.openntf.domino.xots.Tasklet;

@Tasklet(session = Tasklet.Session.CLONE)
/**
 * Interface for Workers that write objects asynchronous
 * @author Roland Praml, FOCONIS AG
 *
 * @param <T>
 */
public interface TaskletWorker<T> {

	public void startUp();

	public void tearDown();

	public void process(T t);

}
