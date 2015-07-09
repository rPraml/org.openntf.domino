package org.openntf.domino.design.impl;

import org.openntf.domino.Database;
import org.openntf.domino.DbDirectory;
import org.openntf.domino.design.DatabaseDesign;
import org.openntf.domino.design.impl.vfs.VFSRootDirectoryNode;
import org.openntf.domino.design.vfs.VFSRootNode;

public class DatabaseDesignService implements org.openntf.domino.design.IDatabaseDesignService {

	@Override
	public DatabaseDesign getDatabaseDesign(final Database database) {
		if (database.isDesignProtected()) {
			return new org.openntf.domino.design.impl.ProtectedDatabaseDesign(database);
		} else {
			return new org.openntf.domino.design.impl.DatabaseDesign(database);
		}
	}

	@Override
	public VFSRootNode getVFS(final DbDirectory dbDirectory) {
		return new VFSRootDirectoryNode(dbDirectory);
	}

}
