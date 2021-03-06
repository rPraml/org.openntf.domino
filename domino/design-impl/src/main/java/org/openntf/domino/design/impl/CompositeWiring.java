/*
 * Copyright 2013
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
 */

package org.openntf.domino.design.impl;

/**
 * @author Roland Praml, FOCONIS AG
 * 
 */
public final class CompositeWiring extends AbstractDesignNapiFileResource implements HasMetadata, org.openntf.domino.design.CompositeWiring {
	private static final long serialVersionUID = 1L;

	@Override
	public void setName(String title) {
		int ind = title.lastIndexOf(".wsdl");
		if (ind >= 0) {
			title = title.substring(0, ind);
		}
		super.setName(title);
	}

	@Override
	protected String getDefaultFlags() {
		return "34567C:Q";
	}

	@Override
	protected String getDefaultFlagsExt() {
		return "";
	}
}
