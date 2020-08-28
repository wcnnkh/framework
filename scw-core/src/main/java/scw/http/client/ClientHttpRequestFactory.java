package scw.http.client;

import java.io.IOException;
import java.net.URI;

import scw.http.HttpMethod;

public interface ClientHttpRequestFactory {
	ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException;
}
