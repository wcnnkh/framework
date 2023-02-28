package io.basc.framework.rpc.remote.web;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.lang.NamedInheritableThreadLocal;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.rpc.remote.DefaultRemoteRequestMessage;
import io.basc.framework.rpc.remote.RemoteMessageCodec;
import io.basc.framework.rpc.remote.RemoteRequestMessage;

public class HttpCallableFactory implements CallableFactory {
	private static ThreadLocal<HttpHeaders> HTTP_HEADERS_LOCAL = new NamedInheritableThreadLocal<HttpHeaders>(
			HttpCallable.class.getSimpleName() + "_headers");

	public static void setLocalHeaders(HttpHeaders headers) {
		if (headers == null) {
			HTTP_HEADERS_LOCAL.remove();
		} else {
			HTTP_HEADERS_LOCAL.set(headers);
		}
	}

	private final HttpClient httpClient;
	private final RemoteMessageCodec messageCodec;
	private final String url;

	public HttpCallableFactory(HttpClient httpClient, RemoteMessageCodec messageCodec, String url) {
		this.httpClient = httpClient;
		this.messageCodec = messageCodec;
		this.url = url;
	}

	public Callable<Object> getCallable(Class<?> clazz, Method method, Object[] args) {
		HttpHeaders httpHeaders = HTTP_HEADERS_LOCAL.get();
		HTTP_HEADERS_LOCAL.remove();
		RemoteRequestMessage requestMessage = new DefaultRemoteRequestMessage(clazz, method, args);
		return new HttpCallable(httpClient, messageCodec, requestMessage, UriUtils.toUri(url), httpHeaders);
	}

}
