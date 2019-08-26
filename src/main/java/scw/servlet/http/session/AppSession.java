package scw.servlet.http.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import scw.core.utils.StringUtils;
import scw.login.LoginFactory;
import scw.login.UserSessionMetaData;
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
	private UserSessionMetaData userSessionMetaData;
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
			userSessionMetaData = loginFactory.getSession(sid);
		}

		if (userSessionMetaData != null && !StringUtils.isNull(uidKey)) {
			if (!userSessionMetaData.getUid().equals(getString(uidKey))) {// uid不一致
				userSessionMetaData = null;
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

	public UserSessionMetaData getSession() {
		return userSessionMetaData;
	}

	public boolean isLogin() {
		return userSessionMetaData != null;
	}

	public UserSessionMetaData login(String uid) {
		userSessionMetaData = loginFactory.login(uid);
		return userSessionMetaData;
	}

	public UserSessionMetaData login(long uid) {
		return login(uid + "");
	}

	public UserSessionMetaData login(int uid) {
		return login(uid + "");
	}

	public void addCookie(HttpServletResponse httpServletResponse) {
		if (userSessionMetaData == null) {
			return;
		}

		httpServletResponse.addCookie(new Cookie(sidKey, userSessionMetaData.getId()));
		if (!StringUtils.isNull(uidKey)) {
			httpServletResponse.addCookie(new Cookie(uidKey, userSessionMetaData.getUid()));
		}
	}
}
