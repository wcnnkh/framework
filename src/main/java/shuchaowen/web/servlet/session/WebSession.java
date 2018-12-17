package shuchaowen.web.servlet.session;

import javax.servlet.http.Cookie;

import shuchaowen.auth.login.Session;
import shuchaowen.auth.login.SessionFactory;
import shuchaowen.common.utils.StringUtils;
import shuchaowen.web.servlet.Request;

/**
 * 此类和AppSession和一样 是为应对小项目中admin和app用同一个项目而写的
 * 
 * @author asus1
 *
 */
public final class WebSession {
	private SessionFactory sessionFactory;
	private Request request;
	private boolean cookie;
	private Session session;
	private String uidKey;
	private String sidKey;

	public WebSession(Request request, SessionFactory sessionFactory) {
		this(request, sessionFactory, null, "sid", true);
	}

	public WebSession(Request request, SessionFactory sessionFactory,
			String uidKey, String sidKey, boolean cookie) {
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
		Session session = sessionFactory.login(uid);
		addCookie(session);
		return session;
	}

	public Session login(long uid) {
		return login(uid + "");
	}

	public Session login(int uid) {
		return login(uid + "");
	}

	public void addCookie(Session session) {
		if (session == null) {
			return;
		}

		request.getResponse().addCookie(new Cookie(sidKey, session.getId()));
		if (!StringUtils.isNull(uidKey)) {
			request.getResponse().addCookie(
					new Cookie(uidKey, session.getUid()));
		}
	}
}
