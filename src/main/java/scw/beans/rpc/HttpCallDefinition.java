package scw.beans.rpc;

import scw.beans.annotation.HttpCall;
import scw.net.http.Method;

public final class HttpCallDefinition {
	public static final String COOKIE = "Cookie";

	private final String[] headers;
	private final boolean form;
	private final Method method;

	public HttpCallDefinition(java.lang.reflect.Method method) {
		HttpCall httpProxy = method.getAnnotation(HttpCall.class);
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
