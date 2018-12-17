package shuchaowen.web.servlet;

import javax.servlet.http.Cookie;

import shuchaowen.auth.login.Session;
import shuchaowen.auth.login.SessionFactory;
import shuchaowen.common.utils.StringUtils;

public final class WebSession {
	private SessionFactory sessionFactory;
	private Request request;
	private boolean cookie;
	private Session session;
	private String uidKey;
	private String sidKey;

	public WebSession(Request request, SessionFactory sessionFactory, String uidKey, String sidKey, boolean cookie) {
		this.request = request;
		this.cookie = cookie;
		this.sessionFactory = sessionFactory;
		this.uidKey = uidKey;
		this.sidKey = sidKey;

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

	public Session login(String uid) {
		return sessionFactory.login(uid);
	}

	public Session login(long uid) {
		return sessionFactory.login(uid);
	}

	public Session login(int uid) {
		return sessionFactory.login(uid);
	}

	public void addCookie(Session session) {
		if(session == null){
			return ;
		}
		
		request.getResponse().addCookie(new Cookie(sidKey, session.getId()));
		if (!StringUtils.isNull(uidKey)) {
			request.getResponse().addCookie(new Cookie(uidKey, session.getUid()));
		}
	}
}
