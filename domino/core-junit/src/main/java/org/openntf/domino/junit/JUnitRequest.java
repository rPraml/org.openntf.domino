package org.openntf.domino.junit;

import java.util.Locale;

import org.openntf.domino.thread.DominoRequest;
import org.openntf.domino.utils.Factory;

import com.ibm.icu.util.TimeZone;

public class JUnitRequest extends DominoRequest {

	public JUnitRequest(final String action, final String runAs) {
		super(action);
		timeZone_ = TimeZone.getTimeZone("America/Los_Angeles");
		locale_ = Locale.US;
		tc_ = Factory.STRICT_THREAD_CONFIG;
		userName_ = runAs;

	}

}
