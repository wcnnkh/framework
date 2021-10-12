package io.basc.framework.mvc;

import java.io.IOException;

public interface HttpChannelInterceptor {
	Object intercept(HttpChannel httpChannel, HttpChannelInterceptorChain chain) throws IOException;
}
