package io.basc.framework.http.client;

import java.net.CookieHandler;
import java.net.URI;
import java.util.Map;

import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.net.uri.UriUtils;

/**
 * @see HttpClient
 * @see DefaultHttpClient
 * @author shuchaowen
 *
 */
public interface HttpConnectionFactory{

	HttpConnection createConnection(HttpMethod method, URI uri);

	default HttpConnection createConnection(HttpMethod method, String url) {
		return createConnection(method, UriUtils.toUri(url));
	}

	HttpConnection createConnection(HttpMethod method, String url, Map<String, ?> uriVariables);

	HttpConnection createConnection(HttpMethod method, String url, Object... uriVariables);

	<T> HttpResponseEntity<T> execute(String httpMethod, URI uri, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor);
}
