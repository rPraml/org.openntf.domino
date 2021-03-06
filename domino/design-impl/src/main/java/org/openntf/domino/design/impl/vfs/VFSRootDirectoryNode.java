/*
 * © Copyright FOCONIS AG, 2014
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 */
package org.openntf.domino.design.impl.vfs;

import java.util.HashSet;
import java.util.Set;

import org.openntf.domino.Database;
import org.openntf.domino.DbDirectory;
import org.openntf.domino.design.vfs.VFSRootNode;

/**
 * The root of a server's dbDirectory
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public class VFSRootDirectoryNode extends VFSDirectoryNode implements VFSRootNode {
	private static final long serialVersionUID = 1L;
	private Set<Database.MetaData> metaDataSet;
	private Set<String> virtualfolders = new HashSet<String>();

	/**
	 * Constructor, called from the DbDirectory
	 * 
	 */
	public VFSRootDirectoryNode(final DbDirectory directory) {
		super(null, "");
		this.metaDataSet = directory.getMetaDataSet();
	}

	@Override
	public void refresh(final DbDirectory directory) {
		directory.clear();
		metaDataSet = directory.getMetaDataSet();
	}

	@Override
	protected void init() {
		for (Database.MetaData metaData : metaDataSet) {

			String fileName = metaData.getFilePath().replace('\\', '/');
			String[] components = fileName.split("/");
			add(components, metaData, 0);
		}
	}

	@Override
	public Set<String> getVirtualFolders() {
		return virtualfolders;
	}

}
