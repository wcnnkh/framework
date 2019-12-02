package scw.security.session.http;

import scw.core.annotation.DefaultValue;
import scw.core.annotation.Order;
import scw.core.annotation.ParameterName;
import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.mvc.http.HttpChannel;
import scw.net.http.Cookie;
import scw.security.session.UserSession;
import scw.security.session.UserSessionFactory;

public class DefaultHttpChannelUserSessionFactory<T> implements HttpChannelUserSessionFactory<T> {
	private UserSessionFactory<T> userSessionFactory;
	private String sessionIdKey;
	private boolean searchCookie;

	@Order
	public DefaultHttpChannelUserSessionFactory(UserSessionFactory<T> userSessionFactory, @ParameterName("http.user.session.key")@DefaultValue("token")String sessionIdKey,
			@ParameterName("http.user.session.cookie")@DefaultValue("false")boolean searchCookie) {
		this.userSessionFactory = userSessionFactory;
		this.sessionIdKey = sessionIdKey;
		this.searchCookie = searchCookie;
	}

	public UserSession<T> getUserSession(HttpChannel httpChannel) {
		String id = httpChannel.getString(sessionIdKey);
		if (id == null && searchCookie) {
			Cookie cookie = httpChannel.getRequest().getCookie(sessionIdKey, true);
			if (cookie != null) {
				id = cookie.getValue();
			}
		}

		if (StringUtils.isEmpty(id)) {
			return null;
		}

		return userSessionFactory.getSession(id);
	}

	public UserSession<T> createUserSession(HttpChannel httpChannel, T uid) {
		UserSession<T> userSession = userSessionFactory.getSession(uid, uid + XUtils.getUUID(), true);
		if (userSession == null) {
			return null;
		}

		if (searchCookie) {
			httpChannel.getResponse().addCookie(sessionIdKey, userSession.getId());
		}

		return userSession;
	}

}
