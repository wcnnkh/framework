package scw.http.client;

import java.net.URI;
import java.util.Map;

import scw.http.HttpMethod;
import scw.net.uri.UriTemplateHandler;
import scw.net.uri.UriUtils;

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

	HttpConnection createConnection(HttpMethod method, String url,
			Map<String, ?> uriVariables);

	HttpConnection createConnection(HttpMethod method, String url,
			Object... uriVariables);
	
	static abstract class AbstractHttpConnectionFactory implements HttpConnectionFactory{
		
		protected abstract UriTemplateHandler getUriTemplateHandler();
		
		public HttpConnection createConnection(HttpMethod method, String url) {
			return createConnection(method, UriUtils.toUri(url));
		}

		public HttpConnection createConnection(HttpMethod method, String url,
				Map<String, ?> uriVariables) {
			return createConnection(method, getUriTemplateHandler().expand(url, uriVariables));
		}

		public HttpConnection createConnection(HttpMethod method, String url,
				Object... uriVariables) {
			return createConnection(method, getUriTemplateHandler().expand(url, uriVariables));
		}
		
	}
}
