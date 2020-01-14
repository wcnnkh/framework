package scw.data.file;

import scw.core.Converter;
import scw.net.http.ClientHttpResponse;
import scw.net.http.Method;
import scw.net.http.SimpleClientHttpRequest;

public class ClientHttpResponseCacheConvert implements Converter<String, ClientHttpResponse> {
	public ClientHttpResponse convert(String url) throws Exception {
		SimpleClientHttpRequest simpleClientHttpRequest = new SimpleClientHttpRequest(Method.GET, url);
		return simpleClientHttpRequest.execute();
	}
}
