package org.openntf.domino.design.vfs;

import org.openntf.domino.Database;

public interface VFSDatabaseNode extends VFSNode {
	
	public void refresh();

	public Database getDatabase();
}
