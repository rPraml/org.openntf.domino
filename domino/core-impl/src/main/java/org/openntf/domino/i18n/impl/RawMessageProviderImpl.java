package org.openntf.domino.i18n.impl;

import org.openntf.domino.commons.i18n.RawMessageProviderResBdlAbstract;
import org.openntf.domino.utils.Factory;

public class RawMessageProviderImpl extends RawMessageProviderResBdlAbstract {

	@Override
	protected ClassLoader getClassLoader() {
		return Factory.getClassLoader();
	}
}
