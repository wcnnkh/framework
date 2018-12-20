package scw.servlet.session;

import javax.servlet.http.Cookie;

import scw.auth.login.Session;
import scw.auth.login.SessionFactory;
import scw.common.utils.StringUtils;
import scw.servlet.Request;

/**
 * 此类和WebSession和一样
 * 是为应对小项目中admin和app用同一个项目而写的
 * @author asus1
 *
 */
public class AppSession {
	private SessionFactory sessionFactory;
	private Request request;
	private boolean cookie;
	private Session session;
	private String uidKey;
	private String sidKey;
	
	public AppSession(Request request, SessionFactory sessionFactory,
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
		session = sessionFactory.login(uid);
		return session;
	}

	public Session login(long uid) {
		return login(uid + "");
	}

	public Session login(int uid) {
		return login(uid + "");
	}

	public void addCookie() {
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
