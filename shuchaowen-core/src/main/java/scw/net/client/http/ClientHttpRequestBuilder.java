package scw.net.client.http;

import scw.net.client.ClientRequestBuilder;
import scw.net.http.HttpMethod;

public interface ClientHttpRequestBuilder extends
		ClientRequestBuilder<ClientHttpRequest> {
	HttpMethod getMethod();
}
