package scw.data.file;

import java.net.URI;

import scw.core.Converter;
import scw.net.client.http.ClientHttpRequest;
import scw.net.client.http.ClientHttpResponse;
import scw.net.client.http.accessor.HttpAccessor;
import scw.net.http.Method;

public class ClientHttpResponseCacheConvert extends HttpAccessor implements Converter<String, ClientHttpResponse> {

	public ClientHttpResponse convert(String url) throws Exception {
		ClientHttpRequest clientHttpRequest = createRequest(new URI(url), Method.GET);
		return clientHttpRequest.execute();
	}
}
