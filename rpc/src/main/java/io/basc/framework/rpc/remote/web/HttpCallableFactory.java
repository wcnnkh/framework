package io.basc.framework.rpc.remote.web;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import io.basc.framework.beans.factory.config.InheritableThreadLocalConfigurator;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.rpc.remote.DefaultRemoteRequestMessage;
import io.basc.framework.rpc.remote.RemoteMessageCodec;
import io.basc.framework.rpc.remote.RemoteRequestMessage;

public class HttpCallableFactory implements CallableFactory {
	private static final InheritableThreadLocalConfigurator<HttpHeaders> HTTP_HEADERS_CONFIGURATOR = new InheritableThreadLocalConfigurator<HttpHeaders>(
			HttpHeaders.class);

	public static InheritableThreadLocalConfigurator<HttpHeaders> getHttpHeadersConfigurator() {
		return HTTP_HEADERS_CONFIGURATOR;
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
		HttpHeaders httpHeaders = HTTP_HEADERS_CONFIGURATOR.get();
		HTTP_HEADERS_CONFIGURATOR.remove();
		RemoteRequestMessage requestMessage = new DefaultRemoteRequestMessage(clazz, method, args);
		return new HttpCallable(httpClient, messageCodec, requestMessage, UriUtils.toUri(url), httpHeaders);
	}

}
