package io.basc.framework.web.pattern;

import io.basc.framework.util.function.Function;
import io.basc.framework.web.ServerHttpRequest;

public class WebProcessorRegistry<T, E extends Throwable> extends HttpPatternMatcher<Function<ServerHttpRequest, T, E>>
		implements ServerHttpRequestAccept, Function<ServerHttpRequest, T, E> {

	@Override
	public T process(ServerHttpRequest request) throws E {
		Function<ServerHttpRequest, T, E> processor = get(request);
		if (processor == null) {
			return null;
		}
		return processor.process(request);
	}

}
