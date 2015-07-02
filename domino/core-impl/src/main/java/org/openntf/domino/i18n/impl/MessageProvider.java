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
package org.openntf.domino.i18n.impl;

import java.util.List;
import java.util.Locale;

import org.openntf.domino.commons.ServiceLocator;
import org.openntf.domino.commons.i18n.MessageProviderAbstract;
import org.openntf.domino.commons.i18n.RawMessageProviderAbstract;
import org.openntf.domino.utils.Factory;

public class MessageProvider extends MessageProviderAbstract {

	static {
		_instance = new MessageProvider();
	}

	@Override
	protected MessageProviderAbstract getMessageProvider() {
		List<MessageProviderAbstract> msgProv = ServiceLocator.findApplicationServices(MessageProviderAbstract.class);
		if (msgProv.size() == 0)
			throw new IllegalStateException("No MessageProvider service found");
		return msgProv.get(0); // we take the first one. 
	}

	@Override
	protected List<RawMessageProviderAbstract> findRawMessageProviders() {
		return ServiceLocator.findApplicationServices(RawMessageProviderAbstract.class);
	}

	@Override
	protected Locale getExternalLocale() {
		return Factory.getExternalLocale();
	}

	@Override
	protected Locale getInternalLocale() {
		return Factory.getInternalLocale();
	}

}
