package io.basc.framework.http.client;

import java.io.IOException;

public interface HttpClientCookieManager {
	void accept(ClientHttpRequest clientHttpRequest) throws IOException;

	void accept(ClientHttpResponse clientHttpResponse) throws IOException;
}
