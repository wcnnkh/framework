package io.basc.framework.rpc.http;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.client.HttpConnection;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.lang.Nullable;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.rpc.remote.web.HttpCallable;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public abstract class HttpConnectionCallableFactory implements CallableFactory {
	private static ThreadLocal<HttpHeaders> HTTP_HEADERS_LOCAL = new NamedThreadLocal<HttpHeaders>(HttpCallable.class.getSimpleName() + "_headers");
	
	public static void setLocalHeaders(HttpHeaders headers){
		if(headers == null){
			HTTP_HEADERS_LOCAL.remove();
		}else{
			HTTP_HEADERS_LOCAL.set(headers);
		}
	}
	
	public Callable<Object> getCallable(Class<?> clazz, Method method,
			Object[] args) {
		HttpConnection connection = getConnection(HTTP_HEADERS_LOCAL.get(), clazz, method, args);
		HTTP_HEADERS_LOCAL.remove();
		return new HttpConnectionCallable(connection,
				TypeDescriptor.forMethodReturnType(method));
	}

	protected abstract HttpConnection getConnection(@Nullable HttpHeaders httpHeaders, Class<?> clazz,
			Method method, Object[] args);
}
