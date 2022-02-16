package io.basc.framework.http.client;

import java.io.IOException;

public interface ClientHttpRequestCallback {
	/**
	 * @param clientRequest
	 * @return 可能返回一个新的(一般是被包装过的)
	 * @throws IOException
	 */
	ClientHttpRequest callback(ClientHttpRequest clientRequest) throws IOException;
}
