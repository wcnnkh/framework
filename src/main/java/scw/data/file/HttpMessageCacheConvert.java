package scw.data.file;

import scw.core.Converter;
import scw.net.HttpMessage;
import scw.net.http.HttpRequest;
import scw.net.http.Method;

public class HttpMessageCacheConvert implements Converter<String, HttpMessage> {
	public HttpMessage convert(String url) throws Exception {
		HttpRequest httpRequest = new HttpRequest(Method.GET, url);
		return httpRequest.execute();
	}
}
