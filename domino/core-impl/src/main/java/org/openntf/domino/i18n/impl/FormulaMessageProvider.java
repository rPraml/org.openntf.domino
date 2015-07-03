/*
 * Â© Copyright FOCONIS AG, 2014
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
package org.openntf.domino.i18n.impl;

import java.util.Locale;

import org.openntf.domino.commons.i18n.FormulaMessageProviderAbstract;
import org.openntf.domino.commons.i18n.IExtIntLocaleProvider;
import org.openntf.domino.utils.Factory;

/** Implements the trivial getLocale methods in openntf environment */
public class FormulaMessageProvider extends FormulaMessageProviderAbstract implements IExtIntLocaleProvider {

	@Override
	protected IExtIntLocaleProvider getExtIntLocaleProvider() {
		return this;
	}

	@Override
	public Locale getExternalLocale() {
		return Factory.getExternalLocale();
	}

	@Override
	public Locale getInternalLocale() {
		return Factory.getInternalLocale();
	}

}
