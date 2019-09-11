package scw.security.session.mvc.http;

import scw.mvc.http.HttpRequest;
import scw.security.session.UserSession;

public interface HttpUserSessionFactory<T> {

	UserSession<T> getUserSession(HttpRequest httpRequest);

	UserSession<T> getUserSession(HttpRequest httpRequest, boolean create);
}
