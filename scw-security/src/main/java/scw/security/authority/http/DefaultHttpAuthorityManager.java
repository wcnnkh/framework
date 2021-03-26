package scw.security.authority.http;

import java.util.HashMap;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.json.JSONUtils;
import scw.lang.AlreadyExistsException;
import scw.security.authority.DefaultAuthorityManager;

public class DefaultHttpAuthorityManager<T extends HttpAuthority> extends
		DefaultAuthorityManager<T> implements HttpAuthorityManager<T> {
	private Map<String, Map<HttpMethod, String>> pathMap = new HashMap<String, Map<HttpMethod, String>>();

	public T getAuthority(String path, HttpMethod method) {
		Map<HttpMethod, String> map = pathMap.get(path);
		if (map == null) {
			return null;
		}

		String id = map.get(method);
		if (id == null) {
			return null;
		}

		return getAuthority(id);
	}

	public synchronized void register(T authority) {
		if (StringUtils.isNotEmpty(authority.getPath())) {
			Map<HttpMethod, String> map = pathMap.get(authority.getPath());
			if (map == null) {
				map = new HashMap<HttpMethod, String>();
			}

			if (map.containsKey(authority.getHttpMethod())) {
				throw new AlreadyExistsException(
						JSONUtils.getJsonSupport().toJSONString(authority));
			}

			map.put(authority.getHttpMethod(), authority.getId());
			pathMap.put(authority.getPath(), map);
		}
		super.register(authority);
	};

}
