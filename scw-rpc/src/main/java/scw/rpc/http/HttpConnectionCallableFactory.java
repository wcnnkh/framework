package scw.rpc.http;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import scw.convert.TypeDescriptor;
import scw.http.HttpHeaders;
import scw.http.client.HttpConnection;
import scw.lang.NamedThreadLocal;
import scw.lang.Nullable;
import scw.rpc.CallableFactory;
import scw.rpc.remote.web.HttpCallable;

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
