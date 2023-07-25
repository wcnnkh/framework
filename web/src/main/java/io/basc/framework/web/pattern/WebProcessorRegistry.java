package io.basc.framework.web.pattern;

import io.basc.framework.util.function.Processor;
import io.basc.framework.web.ServerHttpRequest;

public class WebProcessorRegistry<T, E extends Throwable> extends HttpPatternMatcher<Processor<ServerHttpRequest, T, E>>
		implements ServerHttpRequestAccept, Processor<ServerHttpRequest, T, E> {

	@Override
	public T process(ServerHttpRequest request) throws E {
		Processor<ServerHttpRequest, T, E> processor = get(request);
		if (processor == null) {
			return null;
		}
		return processor.process(request);
	}

}
