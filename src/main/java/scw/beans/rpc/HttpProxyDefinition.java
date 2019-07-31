package scw.beans.rpc;

import scw.beans.annotation.HttpProxy;
import scw.net.http.Method;

public final class HttpProxyDefinition {
	public static final String COOKIE = "Cookie";

	private final String[] headers;
	private final boolean form;
	private final Method method;

	public HttpProxyDefinition(java.lang.reflect.Method method) {
		HttpProxy httpProxy = method.getAnnotation(HttpProxy.class);
		if (httpProxy == null) {
			this.headers = new String[] { COOKIE };
			this.form = true;
			this.method = Method.GET;
		} else {
			this.headers = httpProxy.headers();
			this.form = httpProxy.form();
			this.method = httpProxy.method();
		}
	}

	public String[] getHeaders() {
		return headers;
	}

	public boolean isForm() {
		return form;
	}

	public Method getMethod() {
		return method;
	}
}
