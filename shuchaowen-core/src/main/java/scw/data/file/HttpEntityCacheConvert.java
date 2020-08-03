package scw.data.file;

import scw.core.Converter;
import scw.http.HttpResponseEntity;
import scw.http.HttpUtils;

public class HttpEntityCacheConvert implements
		Converter<String, HttpResponseEntity<byte[]>> {

	public HttpResponseEntity<byte[]> convert(String url) {
		return HttpUtils.getHttpClient().get(byte[].class, url);
	}
}
