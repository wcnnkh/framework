package io.basc.framework.security.authority.http;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.security.authority.DefaultAuthorityManager;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.register.LimitedRegistration;
import io.basc.framework.util.register.Registration;

public class DefaultHttpAuthorityManager<T extends HttpAuthority> extends DefaultAuthorityManager<T>
		implements HttpAuthorityManager<T> {
	private Map<String, Map<String, String>> pathMap = new HashMap<String, Map<String, String>>();

	public T getAuthority(String path, String method) {
		Map<String, String> map = pathMap.get(path);
		if (map == null) {
			return null;
		}

		String id = map.get(method);
		if (id == null) {
			return null;
		}

		return getAuthority(id);
	}

	public void unregister(T authority) {
		if (StringUtils.isEmpty(authority.getPath())) {
			return;
		}

		synchronized (this) {
			Map<String, String> map = pathMap.get(authority.getPath());
			if (map == null) {
				return;
			}
			map.remove(authority.getMethod());
		}
	}

	public Registration register(T authority) {
		Registration registration = Registration.EMPTY;
		if (StringUtils.isNotEmpty(authority.getPath())) {
			synchronized (this) {
				Map<String, String> map = pathMap.get(authority.getPath());
				if (map == null) {
					map = new HashMap<String, String>();
				}

				if (map.containsKey(authority.getMethod())) {
					throw new AlreadyExistsException(authority.toString());
				}

				map.put(authority.getMethod(), authority.getId());
				pathMap.put(authority.getPath(), map);
				registration = registration.and(LimitedRegistration.of(() -> {
					unregister(authority);
				}));
			}
		}

		try {
			return registration.and(super.register(authority));
		} catch (Throwable e) {
			registration.unregister();
			throw e;
		}

	};
}
