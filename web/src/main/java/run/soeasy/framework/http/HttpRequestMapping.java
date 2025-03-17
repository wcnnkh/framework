package run.soeasy.framework.http;

import run.soeasy.framework.net.RequestMapping;
import run.soeasy.framework.util.exchange.Registration;

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
