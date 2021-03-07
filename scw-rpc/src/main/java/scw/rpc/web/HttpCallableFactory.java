package scw.rpc.web;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.Callable;

import scw.convert.TypeDescriptor;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.client.HttpConnection;
import scw.http.client.HttpConnectionFactory;
import scw.lang.NamedThreadLocal;
import scw.net.uri.UriComponentsBuilder;
import scw.rpc.CallableFactory;
import scw.rpc.messageing.RemoteMessageCodec;
import scw.rpc.messageing.RemoteRequestMessage;
import scw.rpc.messageing.support.DefaultRemoteMethodRequestMessage;
import scw.util.MultiValueMap;

public class HttpCallableFactory implements CallableFactory {
	private static ThreadLocal<HttpHeaders> HTTP_HEADERS_LOCAL = new NamedThreadLocal<HttpHeaders>(HttpCallableFactory.class.getSimpleName() + "_headers");
	private static ThreadLocal<MultiValueMap<String, String>> HTTP_PARAMETERS_LOCAL = new NamedThreadLocal<MultiValueMap<String,String>>(HttpCallableFactory.class.getName() + "_parameters");
	
	public static void setLocalHeaders(HttpHeaders headers){
		if(headers == null){
			HTTP_HEADERS_LOCAL.remove();
		}else{
			HTTP_HEADERS_LOCAL.set(headers);
		}
	}
	
	public static void setLocalPaameters(MultiValueMap<String, String> parameters){
		if(parameters == null){
			HTTP_PARAMETERS_LOCAL.remove();
		}else{
			HTTP_PARAMETERS_LOCAL.set(parameters);
		}
	}
	
	private final HttpConnectionFactory httpConnectionFactory;
	private final RemoteMessageCodec messageCodec;
	private final String url;

	public HttpCallableFactory(HttpConnectionFactory httpConnectionFactory,
			RemoteMessageCodec messageCodec, String url) {
		this.httpConnectionFactory = httpConnectionFactory;
		this.messageCodec = messageCodec;
		this.url = url;
	}

	public Callable<Object> getCallable(Class<?> clazz, Method method,
			Object[] args) {
		URI uri = UriComponentsBuilder.fromUriString(url).queryParams(HTTP_PARAMETERS_LOCAL.get()).build().toUri();
		HttpConnection httpConnection = httpConnectionFactory.createConnection(HttpMethod.POST, uri);
		
		HttpHeaders localHeaders = HTTP_HEADERS_LOCAL.get();
		if(localHeaders != null){
			httpConnection.getHeaders().putAll(localHeaders);
		}
		
		RemoteRequestMessage requestMessage = new DefaultRemoteMethodRequestMessage(
				clazz, method, args);
		TypeDescriptor responseType = TypeDescriptor
				.forMethodReturnType(method);
		return new HttpCallable(httpConnection, messageCodec, requestMessage,
				responseType);
	}

}
