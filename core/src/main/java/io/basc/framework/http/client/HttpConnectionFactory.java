package io.basc.framework.http.client;

import java.net.URI;
import java.util.Map;

import io.basc.framework.http.HttpMethod;

/**
 * @see HttpClient
 * @see DefaultHttpClient
 * @author shuchaowen
 *
 */
public interface HttpConnectionFactory {
	HttpConnection createConnection();

	HttpConnection createConnection(HttpMethod method, URI uri);

	HttpConnection createConnection(HttpMethod method, String url);

	HttpConnection createConnection(HttpMethod method, String url, Map<String, ?> uriVariables);

	HttpConnection createConnection(HttpMethod method, String url, Object... uriVariables);
}
