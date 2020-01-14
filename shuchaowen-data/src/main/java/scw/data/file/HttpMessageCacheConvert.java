package scw.data.file;

import scw.core.Converter;
import scw.net.http.HttpRequest;
import scw.net.http.Method;
import scw.net.message.HttpInputMessage;

public class HttpMessageCacheConvert implements Converter<String, HttpInputMessage> {
	public HttpInputMessage convert(String url) throws Exception {
		HttpRequest httpRequest = new HttpRequest(Method.GET, url);
		return httpRequest.execute();
	}
}
