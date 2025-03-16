package io.basc.framework.net;

import io.basc.framework.util.register.container.DefaultMapContainer;
import lombok.Getter;

@Getter
public class RequestMapping<V> extends DefaultMapContainer<RequestPattern, V> {
	private final RequestPatterns excludes = new RequestPatterns();

	public V dispatch(Request request) {
		if (excludes.test(request)) {
			return null;
		}

		if (request instanceof RequestPatternCapable) {
			RequestPattern requestPattern = ((RequestPatternCapable) request).getRequestPattern();
			V value = get(requestPattern);
			if (value != null) {
				return value;
			}
		}
		return entries().filter((e) -> e.getKey().test(request)).map((e) -> e.getValue()).first();
	}
}
