package scw.net.client.http;

import scw.net.client.ClientRequestBuilder;
import scw.net.http.Method;

public interface ClientHttpRequestBuilder extends
		ClientRequestBuilder<ClientHttpRequest> {
	Method getMethod();
}
