package io.basc.framework.data.file;

import io.basc.framework.convert.Converter;
import io.basc.framework.http.HttpUtils;

public class HttpGetBodyCacheConvert implements
		Converter<String, String> {

	public String convert(String url) {
		return HttpUtils.getHttpClient().get(String.class, url).getBody();
	}
}
