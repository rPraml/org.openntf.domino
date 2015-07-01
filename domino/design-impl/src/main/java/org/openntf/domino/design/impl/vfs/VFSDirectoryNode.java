/*
 * Copyright 2015 - FOCONIS AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express o 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 */
package org.openntf.domino.design.impl.vfs;

import org.openntf.domino.Database;
import org.openntf.domino.design.vfs.VFSNode;

/**
 * A directory node that describes a directory in the NSF Path
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public class VFSDirectoryNode extends VFSAbstractNode<Database.MetaData> {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new VFSDirectoryNode
	 * 
	 */
	public VFSDirectoryNode(final VFSNode parent, final String name) {
		super(parent, name);
	}

	/**
	 * Delete is not supported
	 */
	@Override
	public boolean delete() {
		return false;
	}

	/**
	 * As delete is not supported, directory exists always
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/**
	 * Returns always <code>true</code>
	 */
	@Override
	public boolean isDirectory() {
		return true;
	}

	/**
	 * Returns always <code>false</code>
	 */
	@Override
	public boolean isNote() {
		return false;
	}

	/**
	 * Returns always <code>false</code>
	 */
	@Override
	public boolean isDatabase() {
		return false;
	}

	@Override
	public long lastModified() {
		return -1;
	}

	/**
	 * Create a child VFSDirectoryNode
	 */
	@Override
	protected VFSNode newNode(final String name) {
		return new VFSDirectoryNode(this, name);
	}

	/**
	 * Create a child VFSDatabaseNode. The DatabaseNode contains the designElements
	 */
	@Override
	protected VFSNode newLeaf(final String name, final Database.MetaData t) {
		return new VFSDatabaseNode(this, name, t);
	}

}
