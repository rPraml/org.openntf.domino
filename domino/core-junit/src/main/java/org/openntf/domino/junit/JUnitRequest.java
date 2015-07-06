package org.openntf.domino.junit;

import java.util.Locale;

import org.openntf.domino.thread.DominoRequest;
import org.openntf.domino.utils.Factory.ThreadConfig;

public class JUnitRequest extends DominoRequest {

	public JUnitRequest(final ThreadConfig tc, final String suffix, final String userName) {
		super(tc, suffix, Locale.US);
		this.userName = userName;
	}

}
