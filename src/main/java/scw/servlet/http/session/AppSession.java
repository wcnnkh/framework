package scw.servlet.http.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import scw.core.utils.StringUtils;
import scw.login.LoginFactory;
import scw.login.Session;
import scw.servlet.http.HttpRequest;

/**
 * 此类和WebSession和一样 是为应对小项目中admin和app用同一个项目而写的
 * 
 * @author asus1
 *
 */
public class AppSession {
	private LoginFactory loginFactory;
	private HttpRequest request;
	private boolean cookie;
	private Session session;
	private String uidKey;
	private String sidKey;

	public AppSession(HttpRequest request, LoginFactory loginFactory, String uidKey,
			String sidKey, boolean cookie) {
		this.request = request;
		this.cookie = cookie;
		this.loginFactory = loginFactory;
		this.uidKey = uidKey;
		this.sidKey = sidKey;

		if (StringUtils.isNull(sidKey)) {
			throw new NullPointerException("sidKey");
		}

		String sid = getString(sidKey);
		if (!StringUtils.isNull(sid)) {
			session = loginFactory.getSession(sid);
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
		session = loginFactory.login(uid);
		return session;
	}

	public Session login(long uid) {
		return login(uid + "");
	}

	public Session login(int uid) {
		return login(uid + "");
	}

	public void addCookie(HttpServletResponse httpServletResponse) {
		if (session == null) {
			return;
		}

		httpServletResponse.addCookie(new Cookie(sidKey, session.getId()));
		if (!StringUtils.isNull(uidKey)) {
			httpServletResponse.addCookie(new Cookie(uidKey, session.getUid()));
		}
	}
}
