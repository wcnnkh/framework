package io.basc.framework.data.file;

import io.basc.framework.http.HttpUtils;
import io.basc.framework.util.stream.Processor;

public class HttpGetBodyCacheConvert implements Processor<String, String, RuntimeException> {

	public String process(String url) {
		return HttpUtils.getHttpClient().get(String.class, url).getBody();
	}
}
