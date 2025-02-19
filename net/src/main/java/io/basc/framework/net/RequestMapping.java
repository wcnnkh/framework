package io.basc.framework.net;

import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.register.container.DefaultMapContainer;
import lombok.Getter;

@Getter
public class RequestMapping<V> extends DefaultMapContainer<RequestPattern, V> {
	private final RequestPatterns excludes = new RequestPatterns();

	public Registration register(String path, V value) {
		PathPattern pathPattern = new PathPattern();
		pathPattern.setPath(path);
		return register(pathPattern, value);
	}

	public Registration exclude(String path) {
		return getExcludes().register(path);
	}

	public V dispatch(Request request) {
		if (excludes.test(request)) {
			return null;
		}

		RequestPattern requestPattern = request.getRequestPattern();
		V server = get(requestPattern);
		if (server == null) {
			server = entries().filter((e) -> e.getKey().test(request)).map((e) -> e.getValue()).first();
		}
		return server;
	}
}
