package scw.security.authority.http;

import scw.beans.annotation.AutoImpl;
import scw.net.http.HttpMethod;
import scw.security.authority.AuthorityManager;

@AutoImpl({XmlHttpAuthorityManager.class, SimpleHttpAuthorityManager.class})
public interface HttpAuthorityManager extends AuthorityManager<HttpAuthority> {
	HttpAuthority getAuthority(String path, HttpMethod method);
}
