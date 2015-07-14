package org.openntf.domino.xsp;

import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.openntf.domino.thread.DominoRequest;
import org.openntf.domino.utils.Factory.ThreadConfig;

import com.ibm.xsp.context.FacesContextEx;

public class XspRequest extends DominoRequest {

	private transient FacesContextEx fc_;

	public XspRequest(final ThreadConfig tc, final FacesContextEx fc) {
		super(((HttpServletRequest) fc.getExternalContext().getRequest()).getRequestURI());
		this.tc_ = tc;
		this.fc_ = fc;
	}

	@Override
	public Locale getLocale() {
		if (locale_ != null)
			return locale_;

		locale_ = fc_.getSessionData().getLocale();
		if (locale_ != null)
			return locale_;

		Iterator<Locale> locIt = fc_.getExternalContext().getRequestLocales();
		if (locIt.hasNext()) {
			locale_ = locIt.next();
		}
		if (locale_ != null)
			return locale_;
		locale_ = fc_.getExternalContext().getRequestLocale();
		if (locale_ != null)
			return locale_;
		locale_ = fc_.getApplication().getDefaultLocale();
		if (locale_ != null)
			return locale_;

		locale_ = Locale.getDefault();

		return locale_;
	}

	// TODO: How can we handle the browser's TimeZone?
}
