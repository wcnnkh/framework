package scw.http.client;

import java.io.IOException;
import java.net.URI;

import scw.http.HttpMethod;

public interface ClientHttpRequestFactory {
	ClientHttpRequest createRequest(URI url, HttpMethod httpMethod) throws IOException;
}
