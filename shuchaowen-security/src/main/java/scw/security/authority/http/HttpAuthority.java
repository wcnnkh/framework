package scw.security.authority.http;

import scw.net.http.HttpMethod;
import scw.security.authority.Authority;

public interface HttpAuthority extends Authority {
	String getPath();

	HttpMethod getHttpMethod();
}
