package scw.security.authority.http;

import java.util.HashMap;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.json.JSONUtils;
import scw.lang.AlreadyExistsException;
import scw.net.http.Method;
import scw.security.authority.SimpleAuthorityManager;

public class SimpleHttpAuthorityManager extends SimpleAuthorityManager<HttpAuthority> implements HttpAuthorityManager {
	private Map<String, Map<Method, String>> pathMap = new HashMap<String, Map<Method, String>>();

	public HttpAuthority getAuthority(String path, Method method) {
		Map<Method, String> map = pathMap.get(path);
		if (map == null) {
			return null;
		}

		String id = map.get(method);
		if (id == null) {
			return null;
		}

		return getAuthority(id);
	}

	public synchronized void addAuthroity(HttpAuthority authority) {
		if (StringUtils.isNotEmpty(authority.getPath())) {
			Map<Method, String> map = pathMap.get(authority.getPath());
			if (map == null) {
				map = new HashMap<Method, String>();
			}

			if (map.containsKey(authority.getHttpMethod())) {
				throw new AlreadyExistsException(JSONUtils.toJSONString(authority));
			}

			map.put(authority.getHttpMethod(), authority.getId());
			pathMap.put(authority.getPath(), map);
		}
		super.addAuthroity(authority);
	};

}
