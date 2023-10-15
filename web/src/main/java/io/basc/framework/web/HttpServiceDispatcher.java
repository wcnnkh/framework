package io.basc.framework.web;

public interface HttpServiceDispatcher extends WebServiceDispatcher, HttpService {
	@Override
	default boolean test(ServerRequest serverRequest) {
		if (serverRequest instanceof ServerHttpRequest) {
			return test((ServerHttpRequest) serverRequest);
		}
		return false;
	}

	boolean test(ServerHttpRequest serverHttpRequest);
}
