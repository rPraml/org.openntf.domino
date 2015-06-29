package org.openntf.domino.design;

import org.openntf.domino.Database;
import org.openntf.domino.DbDirectory;

public interface DatabaseDesignService {
	DatabaseDesign getDatabaseDesign(Database database);

	VFSRootNode getVFS(DbDirectory dbDirectory);
}
