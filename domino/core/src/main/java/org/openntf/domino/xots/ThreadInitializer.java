package org.openntf.domino.xots;

import lotus.notes.NotesThread;

import org.openntf.domino.commons.utils.ThreadUtils;

public class ThreadInitializer {

	private ClassLoader oldCl;

	private ClassLoader threadCl = ThreadUtils.getContextClassLoader();

	public void initThread() {
		oldCl = ThreadUtils.setContextClassLoader(threadCl);
		NotesThread.sinitThread();
	}

	public void termThread() {
		NotesThread.stermThread();
		if (oldCl != null) {
			ThreadUtils.setContextClassLoader(oldCl);
		}
	}

}
