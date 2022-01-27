package io.basc.framework.http.client;

import java.util.Map;

import io.basc.framework.http.HttpMethod;
import io.basc.framework.net.uri.UriTemplateHandler;
import io.basc.framework.net.uri.UriUtils;

public abstract class AbstractHttpConnectionFactory implements HttpConnectionFactory {

	protected abstract UriTemplateHandler getUriTemplateHandler();

	public HttpConnection createConnection(HttpMethod method, String url) {
		return createConnection(method, UriUtils.toUri(url));
	}

	public HttpConnection createConnection(HttpMethod method, String url, Map<String, ?> uriVariables) {
		return createConnection(method, getUriTemplateHandler().expand(url, uriVariables));
	}

	public HttpConnection createConnection(HttpMethod method, String url, Object... uriVariables) {
		return createConnection(method, getUriTemplateHandler().expand(url, uriVariables));
	}
}