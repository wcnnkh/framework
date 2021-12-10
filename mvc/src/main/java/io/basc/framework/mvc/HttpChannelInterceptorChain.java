package io.basc.framework.mvc;

import java.io.IOException;
import java.util.Iterator;

import io.basc.framework.lang.Nullable;

public class HttpChannelInterceptorChain implements HttpChannelService {
	private final Iterator<HttpChannelInterceptor> iterator;
	private final HttpChannelService service;

	public HttpChannelInterceptorChain(Iterator<HttpChannelInterceptor> iterator,
			@Nullable HttpChannelService service) {
		this.iterator = iterator;
		this.service = service;
	}

	public Object service(HttpChannel httpChannel) throws IOException {
		if (iterator.hasNext()) {
			return iterator.next().intercept(httpChannel, this);
		} else if (service != null) {
			return service.service(httpChannel);
		}
		return null;
	}
}
