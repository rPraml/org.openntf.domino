package org.openntf.domino.thread;

import java.util.Locale;

import org.openntf.domino.Database;
import org.openntf.domino.Session;
import org.openntf.domino.commons.impl.RequestImpl;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.utils.Factory.ThreadConfig;

import com.ibm.icu.util.TimeZone;

public class DominoRequest extends RequestImpl {

	protected ThreadConfig tc_;

	public DominoRequest(final String userName, final Locale locale, final TimeZone timeZone) {
		super(userName, locale, timeZone);
	}

	public DominoRequest(final String action) {
		super(action);
	}

	public DominoRequest threadConfig(final ThreadConfig tc) {
		tc_ = tc;
		return this;
	}

	public ThreadConfig getThreadConfig() {
		return tc_;
	}

	@Override
	public String getUserName() {
		if (userName_ == null) {
			Session sess = Factory.getSession_unchecked(SessionType.CURRENT);
			if (sess != null) {
				userName_ = sess.getEffectiveUserName();
			} else {
				userName_ = "unknown";
			}
		}
		return userName_;
	}

	@Override
	public String getDatabaseContext() {
		if (dbContext_ == null) {
			dbContext_ = "unknown";
			Session sess = Factory.getSession_unchecked(SessionType.CURRENT);
			if (sess != null) {
				Database db = sess.getCurrentDatabase();
				if (db != null) {
					dbContext_ = db.getApiPath();
				}
			}
		}
		return dbContext_;
	}
}
