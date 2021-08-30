package io.basc.framework.security.authority.http;

import io.basc.framework.http.HttpMethod;
import io.basc.framework.security.authority.DefaultAuthority;

import java.util.Map;

public class DefaultHttpAuthority extends DefaultAuthority implements
		HttpAuthority {
	private static final long serialVersionUID = 1L;
	private final String path;
	private final String httpMethod;

	public DefaultHttpAuthority(String id, String parentId, String name,
			Map<String, String> attributeMap, boolean menu, String path, String httpMethod) {
		super(id, parentId, name, attributeMap, menu);
		this.path = path;
		this.httpMethod = httpMethod;
	}

	public String getPath() {
		return path;
	}

	public String getMethod() {
		return httpMethod == null ? HttpMethod.GET.name() : httpMethod;
	}
}
