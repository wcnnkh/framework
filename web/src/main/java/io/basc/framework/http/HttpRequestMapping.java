package io.basc.framework.http;

import io.basc.framework.net.RequestMapping;
import io.basc.framework.util.exchange.Registration;

public class HttpRequestMapping<V> extends RequestMapping<V> {
	public Registration exclude(String path, String method) {
		HttpPattern httpPattern = new HttpPattern();
		httpPattern.setPath(path);
		httpPattern.setMethod(method);
		return getExcludes().register(httpPattern);
	}

	public Registration register(String path, String method, V value) {
		HttpPattern httpPattern = new HttpPattern();
		httpPattern.setPath(path);
		httpPattern.setMethod(method);
		return register(httpPattern, value);
	}
}
