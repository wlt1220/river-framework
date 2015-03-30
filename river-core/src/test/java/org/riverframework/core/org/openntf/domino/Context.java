package org.riverframework.core.org.openntf.domino;

import org.riverframework.Modules;
import org.riverframework.RiverFramework;
import org.riverframework.Session;
import org.riverframework.core.Credentials;

public final class Context extends org.riverframework.core.AbstractContext {
	@Override
	public String getConfigurationFileName() {
		return "test-configuration-org-openntf-domino";
	}

	@Override
	public Session getSession() {
		Session session = RiverFramework.getSession(
				Modules.MODULE_ORG_OPENNTF_DOMINO,
				null, null, Credentials.getPassword());
		return session;
	}

	@Override
	public void closeSession() {
		RiverFramework.closeSession(Modules.MODULE_ORG_OPENNTF_DOMINO);
	}
}