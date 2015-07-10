package org.openntf.domino.design;

import org.openntf.domino.Database;
import org.openntf.domino.DbDirectory;
import org.openntf.domino.commons.ServiceLocator;
import org.openntf.domino.design.vfs.VFSRootNode;

public interface IDatabaseDesignService {

	public enum $ {
		;
		private static IDatabaseDesignService INSTANCE = ServiceLocator.findApplicationService(IDatabaseDesignService.class);

		public static IDatabaseDesignService getInstance() {
			return INSTANCE;
		}
	}

	DatabaseDesign getDatabaseDesign(Database database);

	VFSRootNode getVFS(DbDirectory dbDirectory);
}
