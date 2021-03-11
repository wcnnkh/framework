package scw.rpc.remote.web;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import scw.http.HttpHeaders;
import scw.http.client.ClientHttpRequestFactory;
import scw.lang.NamedThreadLocal;
import scw.net.uri.UriUtils;
import scw.rpc.CallableFactory;
import scw.rpc.remote.DefaultRemoteRequestMessage;
import scw.rpc.remote.RemoteMessageCodec;
import scw.rpc.remote.RemoteRequestMessage;

public class HttpCallableFactory implements CallableFactory {
	private static ThreadLocal<HttpHeaders> HTTP_HEADERS_LOCAL = new NamedThreadLocal<HttpHeaders>(HttpCallable.class.getSimpleName() + "_headers");
	
	public static void setLocalHeaders(HttpHeaders headers){
		if(headers == null){
			HTTP_HEADERS_LOCAL.remove();
		}else{
			HTTP_HEADERS_LOCAL.set(headers);
		}
	}
	
	private final ClientHttpRequestFactory clientHttpRequestFactory;
	private final RemoteMessageCodec messageCodec;
	private final String url;

	public HttpCallableFactory(ClientHttpRequestFactory clientHttpRequestFactory,
			RemoteMessageCodec messageCodec, String url) {
		this.clientHttpRequestFactory = clientHttpRequestFactory;
		this.messageCodec = messageCodec;
		this.url = url;
	}

	public Callable<Object> getCallable(Class<?> clazz, Method method,
			Object[] args) {
		HttpHeaders httpHeaders = HTTP_HEADERS_LOCAL.get();
		HTTP_HEADERS_LOCAL.remove();
		RemoteRequestMessage requestMessage = new DefaultRemoteRequestMessage(
				clazz, method, args);
		return new HttpCallable(clientHttpRequestFactory, messageCodec, requestMessage, UriUtils.toUri(url), httpHeaders);
	}

}
