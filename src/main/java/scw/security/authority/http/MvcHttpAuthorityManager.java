package scw.security.authority.http;

import scw.beans.annotation.AutoImpl;
import scw.mvc.http.HttpRequest;

@AutoImpl(DefaultHttpAuthorityManager.class)
public interface MvcHttpAuthorityManager extends HttpAuthorityManager {
	HttpAuthority getHttpAuthority(HttpRequest httpRequest);
}
