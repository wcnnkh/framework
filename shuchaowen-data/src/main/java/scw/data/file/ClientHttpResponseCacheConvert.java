package scw.data.file;

import java.net.URI;

import scw.core.Converter;
import scw.net.http.Method;
import scw.net.http.client.ClientHttpRequest;
import scw.net.http.client.ClientHttpResponse;
import scw.net.http.client.accessor.HttpAccessor;

public class ClientHttpResponseCacheConvert extends HttpAccessor implements Converter<String, ClientHttpResponse> {

	public ClientHttpResponse convert(String url) throws Exception {
		ClientHttpRequest clientHttpRequest = createRequest(new URI(url), Method.GET);
		return clientHttpRequest.execute();
	}
}
