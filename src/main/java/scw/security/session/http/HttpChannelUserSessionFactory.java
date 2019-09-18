package scw.security.session.http;

import scw.beans.annotation.AutoConfig;
import scw.mvc.http.HttpChannel;
import scw.security.session.UserSession;

@AutoConfig(service=DefaultHttpChannelUserSessionFactory.class)
public interface HttpChannelUserSessionFactory<T> {
	UserSession<T> getUserSession(HttpChannel httpChannel);

	UserSession<T> createUserSession(HttpChannel httpChannel, T uid);
}
