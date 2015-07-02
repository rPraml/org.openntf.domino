package org.openntf.domino.design;

import org.openntf.domino.ext.NoteClass;

public interface DesignMapping {

	int[] getAssistFilter();

	Boolean getFilterXsp();

	public String getFlags();

	public String getFlagsExt();

	public Class<? extends DesignBase> getImplClass();

	public boolean getInclude();

	public Class<? extends DesignBase> getInterfaceClass();

	public NoteClass getNoteClass();

	public String getOnDiskFileExtension();

	public String getOnDiskFolder();

}
