package scw.mvc.http.session;

import scw.core.utils.StringUtils;
import scw.login.LoginFactory;
import scw.login.UserSessionMetaData;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpResponse;
import scw.net.http.Cookie;
import scw.net.http.SimpleCookie;

/**
 * 此类和WebSession和一样 是为应对小项目中admin和app用同一个项目而写的
 * 
 * @author asus1
 *
 */
public class AppSession {
	private LoginFactory loginFactory;
	private HttpChannel channel;
	private boolean cookie;
	private UserSessionMetaData userSessionMetaData;
	private String uidKey;
	private String sidKey;

	public AppSession(HttpChannel channel, LoginFactory loginFactory, String uidKey,
			String sidKey, boolean cookie) {
		this.channel = channel;
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
		String v = channel.getString(name);
		if (v == null && cookie) {
			Cookie cookie = channel.getRequest().getCookie(name, false);
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

	public void addCookie(HttpResponse httpResponse) {
		if (userSessionMetaData == null) {
			return;
		}

		httpResponse.addCookie(new SimpleCookie(sidKey, userSessionMetaData.getId()));
		if (!StringUtils.isNull(uidKey)) {
			httpResponse.addCookie(new SimpleCookie(uidKey, userSessionMetaData.getUid()));
		}
	}
}
