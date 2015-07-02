package org.openntf.domino.commons.i18n;

import java.util.Locale;

public abstract class RawMessageProviderAbstract implements Comparable<RawMessageProviderAbstract> {
	public abstract String getRawText(final String bundleName, final String key, final Locale loc);

	protected int getPriority() {
		return 50;
	}

	@Override
	public int compareTo(final RawMessageProviderAbstract paramT) {
		return getPriority() - paramT.getPriority();
	}

	public void resetCache() {
	}
}
