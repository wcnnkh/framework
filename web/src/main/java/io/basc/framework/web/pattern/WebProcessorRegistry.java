package io.basc.framework.web.pattern;

import io.basc.framework.util.Pipeline;
import io.basc.framework.web.ServerHttpRequest;

public class WebProcessorRegistry<T, E extends Throwable> extends HttpPatternMatcher<Pipeline<ServerHttpRequest, T, E>>
		implements ServerHttpRequestAccept, Pipeline<ServerHttpRequest, T, E> {

	@Override
	public T process(ServerHttpRequest request) throws E {
		Pipeline<ServerHttpRequest, T, E> processor = get(request);
		if (processor == null) {
			return null;
		}
		return processor.process(request);
	}

}
