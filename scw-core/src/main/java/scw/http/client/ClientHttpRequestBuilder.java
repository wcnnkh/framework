package scw.http.client;

import java.io.IOException;
import java.net.URI;

import scw.http.HttpMethod;

public interface ClientHttpRequestBuilder {
	HttpMethod getMethod();
	
	URI getUri();

	ClientHttpRequest builder() throws IOException;
}
