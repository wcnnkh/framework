package scw.net.client.http;

import java.io.IOException;
import java.net.URI;

import scw.net.http.HttpMethod;

public interface ClientHttpRequestFactory {
	ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException;
}
