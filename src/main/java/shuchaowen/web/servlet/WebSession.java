package shuchaowen.web.servlet;

import javax.servlet.http.Cookie;

import shuchaowen.auth.login.Session;
import shuchaowen.auth.login.SessionFactory;
import shuchaowen.common.utils.StringUtils;

public final class WebSession {
	private Request request;
	private boolean cookie;
	private Session session;

	public WebSession(Request request, SessionFactory sessionFactory,
			String uidKey, String sidKey, boolean cookie) {
		this.request = request;
		this.cookie = cookie;

		if (StringUtils.isNull(sidKey)) {
			throw new NullPointerException("sidKey");
		}

		String sid = getString(sidKey);
		if (!StringUtils.isNull(sid)) {
			session = sessionFactory.getSession(sid);
		}

		if (session != null && !StringUtils.isNull(uidKey)) {
			if (!session.getUid().equals(getString(uidKey))) {// uid不一致
				session = null;
			}
		}
	}

	private String getString(String name) {
		String v = request.getString(name);
		if (v == null && cookie) {
			Cookie cookie = request.getCookie(name, false);
			if (cookie != null) {
				v = cookie.getValue();
			}
		}
		return v;
	}

	public Session getSession() {
		return session;
	}

	public boolean isLogin() {
		return session != null;
	}
}
