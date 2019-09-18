package scw.security.session.http;

import scw.beans.annotation.Bean;
import scw.mvc.http.HttpChannel;
import scw.security.session.Authorization;
import scw.security.session.Session;
import scw.security.session.UserSession;

@Bean(singleton=false)
public class HttpChannelAuthorization<T> implements Authorization<T> {
	private HttpChannel httpChannel;
	private HttpChannelUserSessionFactory<T> httpChannelUserSessionFactory;
	private UserSession<T> userSession;

	public HttpChannelAuthorization(HttpChannel httpChannel,
			HttpChannelUserSessionFactory<T> httpChannelUserSessionFactory) {
		this.httpChannel = httpChannel;
		this.httpChannelUserSessionFactory = httpChannelUserSessionFactory;
		this.userSession = httpChannelUserSessionFactory.getUserSession(httpChannel);
	}

	public T getUid() {
		return userSession == null ? null : userSession.getUid();
	}

	public Session getSession() {
		return userSession;
	}

	public void authorization(T uid) {
		this.userSession = httpChannelUserSessionFactory.createUserSession(httpChannel, uid);
	}

}
