package scw.http.client;

import java.io.IOException;

public interface ClientHttpRequestInterceptor {
	ClientHttpResponse intercept(ClientHttpRequest request,
			ClientHttpRequestInterceptorChain chain) throws IOException;
}
