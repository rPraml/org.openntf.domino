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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.openntf.domino.Document;
import org.openntf.domino.design.DxlConverter;
import org.openntf.domino.utils.ODAUtils;

/**
 * @author jgallagher
 * 
 */
public final class StyleSheet extends AbstractDesignNapiFileResource implements org.openntf.domino.design.StyleSheet, HasMetadata {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final Logger log_ = Logger.getLogger(StyleSheet.class.getName());

	@Override
	public String getContent() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			getFileData(bos);
			return new String(bos.toByteArray(), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ODAUtils.handleException(e);
			return null;
		}
	}

	@Override
	public void setContent(final String content) {
		fileData = content.getBytes(Charset.forName("UTF-8"));
	}

	@Override
	protected String getDefaultFlags() {
		return "=34QC";
	}

	@Override
	protected String getDefaultFlagsExt() {
		return "D";
	}

	@Override
	protected void saveData(final DxlConverter converter, final Document doc) throws IOException {
		setValue(doc, "$MimeCharSet", "UTF-8");
		super.saveData(converter, doc);
	}

}