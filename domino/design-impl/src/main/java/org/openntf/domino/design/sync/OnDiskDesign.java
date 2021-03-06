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

package org.openntf.domino.design.sync;

import java.io.File;
import java.net.URI;

import org.openntf.domino.Document;
import org.openntf.domino.commons.Strings;
import org.openntf.domino.design.DesignBase;
import org.openntf.domino.design.DesignBaseNamed;
import org.openntf.domino.design.DesignMapping;
import org.openntf.domino.design.impl.DesignFactory;

/**
 * 
 * @author Alexander Wagner, FOCONIS AG
 * 
 */
public class OnDiskDesign extends OnDiskAbstract<DesignBase> {

	private static final long serialVersionUID = -3298261314433290242L;

	private String name_;
	/** contains the name and variant (hidden notes/web) */
	private String keysuffix_;
	private DesignFactory odpMapping;

	public OnDiskDesign(final File parent, final File file) {
		super(parent, file);
	}

	@Override
	public void setFile(final File parent, final File file) {
		super.setFile(parent, file);
		// example:
		// parent 	= C:\documents\odp\
		// file 	= C:\documents\odp\Code\Scriptlibraries\lib.lss
		// odpFolder= C:\documents\odp\Code\Scriptlibraries
		// relUri 	= lib.lss
		odpMapping = DesignFactory.valueOf(parent, file);
		File odpFolder = new File(parent, odpMapping.getOnDiskFolder());
		URI relUri = odpFolder.toURI().relativize(file.toURI());

		String ext = odpMapping.getOnDiskFileExtension();

		if (ext == null) {
			// no extension, so use the relative file uri
			keysuffix_ = relUri.getPath();
		} else if (ext.equals("*")) {
			// name is "*", so use the unescaped part.
			keysuffix_ = relUri.getPath();
		} else if (ext.startsWith(".")) {
			keysuffix_ = relUri.getPath();
		} else {
			keysuffix_ = ext;
		}

		name_ = keysuffix_;

		if (name_.indexOf(" [") != -1) { // remove variant (hidden opts + language)
			name_ = name_.replaceFirst(" \\[.*\\]", "");
		}
	}

	public String getName() {
		return name_;
	}

	public Class<?> getImplementingClass() {
		return odpMapping.getImplClass();
	}

	@Override
	public String getKey() {
		return (odpMapping.getImplClass().getName() + ":" + keysuffix_).toLowerCase();
	}

	public static String getOnDiskName(final DesignBase design) {
		DesignMapping mapping = design.getMapping();
		String odpExt = mapping.getOnDiskFileExtension();
		String ret;
		String variant = "";

		if (design instanceof DesignBaseNamed) {
			ret = ((DesignBaseNamed) design).getName();

			// variant (hidden, language)
			String language = design.getLanguage();
			if (design.isHideFromWeb() || design.isHideFromNotes() || !Strings.isEmptyString(language)) {
				variant = " [";
				if (design.isHideFromNotes()) {
					variant += "-n";
				}
				if (design.isHideFromWeb()) {
					variant += "-w";
				}
				if (variant.length() > 2 && !Strings.isEmptyString(language)) {
					variant += ",";
				}
				variant += language;
				variant += "]";
			}
		} else {
			ret = design.getUniversalID();
		}

		if (odpExt == null) {
			// no name specified - so encode the current name
			ret = OnDiskUtil.encodeResourceName(ret) + variant;
		} else if (odpExt.equals("*")) {
			return ret; // * means no encoding/translation
		} else if (!odpExt.startsWith(".")) {
			return odpExt; // does not start with . -> Element is not named
		} else {
			ret = OnDiskUtil.encodeResourceName(ret);

			if (!ret.endsWith(odpExt)) {
				ret = ret + variant + odpExt;
			} else {
				if (variant.length() > 0) {
					ret = ret.substring(0, ret.length() - odpExt.length()) + variant + odpExt;
				}
			}
		}
		return ret;
	}

	public static String getOnDiskPath(final DesignBase design) {
		String folder = design.getMapping().getOnDiskFolder();
		if (folder.length() == 0) {
			return OnDiskDesign.getOnDiskName(design);
		}
		return folder + "/" + OnDiskDesign.getOnDiskName(design);
	}

	public static String getKey(final DesignBase design) {
		return (design.getClass().getName() + ":" + getOnDiskName(design)).toLowerCase();
	}

	@Override
	public void setDbTimeStamp(final DesignBase dbElem) {
		Document doc = dbElem.getDocument();
		if (doc == null) {
			throw new IllegalAccessError("The element is not visible; " + dbElem);
		}
		setDbTimeStamp(doc.getLastModifiedDate().getTime());
	}

	@Override
	public long getDbTimeStampDelta(final DesignBase dbElem) {
		Document doc = dbElem.getDocument();
		if (doc == null) {
			throw new IllegalAccessError("The element is not visible; " + dbElem);
		}
		return doc.getLastModifiedDate().getTime() - getDbTimeStamp();
	}

}
