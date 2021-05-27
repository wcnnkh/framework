package scw.security.authority.http;

import java.util.Map;

import scw.http.HttpMethod;
import scw.security.authority.DefaultAuthority;

public class DefaultHttpAuthority extends DefaultAuthority implements
		HttpAuthority {
	private static final long serialVersionUID = 1L;
	private final String path;
	private final HttpMethod httpMethod;

	public DefaultHttpAuthority(String id, String parentId, String name,
			Map<String, String> attributeMap, boolean menu, String path, HttpMethod httpMethod) {
		super(id, parentId, name, attributeMap, menu);
		this.path = path;
		this.httpMethod = httpMethod;
	}

	public String getPath() {
		return path;
	}

	public HttpMethod getMethod() {
		return httpMethod == null ? HttpMethod.GET : httpMethod;
	}
}
