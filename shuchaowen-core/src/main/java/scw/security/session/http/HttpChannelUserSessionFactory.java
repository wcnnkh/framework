package scw.security.session.http;

import scw.beans.annotation.AutoImpl;
import scw.mvc.http.HttpChannel;
import scw.security.session.UserSession;

@AutoImpl(DefaultHttpChannelUserSessionFactory.class)
public interface HttpChannelUserSessionFactory<T> {
	UserSession<T> getUserSession(HttpChannel httpChannel);

	UserSession<T> createUserSession(HttpChannel httpChannel, T uid);
}
