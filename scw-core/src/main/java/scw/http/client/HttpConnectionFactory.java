package scw.http.client;

import java.net.URI;
import java.util.Map;

import scw.http.HttpMethod;
import scw.net.InetUtils;
import scw.net.uri.UriTemplateHandler;

public interface HttpConnectionFactory {
	HttpConnection createConnection();
	
	HttpConnection createConnection(HttpMethod method, URI url);

	HttpConnection createConnection(HttpMethod method, String url);

	HttpConnection createConnection(HttpMethod method, String url,
			Map<String, ?> uriVariables);

	HttpConnection createConnection(HttpMethod method, String url,
			Object... uriVariables);
	
	static abstract class AbstractHttpConnectionFactory implements HttpConnectionFactory{
		
		protected abstract UriTemplateHandler getUriTemplateHandler();
		
		public HttpConnection createConnection(HttpMethod method, String url) {
			return createConnection(method, InetUtils.toURI(url));
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
