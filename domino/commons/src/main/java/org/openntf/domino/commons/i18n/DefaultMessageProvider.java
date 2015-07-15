package org.openntf.domino.commons.i18n;

import java.util.Locale;

import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.LifeCycleManager;

public class DefaultMessageProvider extends MessageProviderAbstract {

	@Override
	public Locale getExternalLocale() {
		IRequest request = LifeCycleManager.getCurrentRequest();
		return request == null ? Locale.getDefault() : request.getLocale();
	}

	@Override
	public Locale getInternalLocale() {
		return Locale.getDefault();
	}

}
