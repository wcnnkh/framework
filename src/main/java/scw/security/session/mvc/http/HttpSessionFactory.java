package scw.security.session.mvc.http;

import scw.mvc.http.HttpRequest;
import scw.security.session.Session;

public interface HttpSessionFactory {
	Session getSession(HttpRequest httpRequest);

	Session getSession(HttpRequest httpRequest, boolean create);
}
