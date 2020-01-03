package scw.mvc.rpc.http;

import java.lang.reflect.Method;

import scw.net.http.HttpRequest;

public interface HttpRpcRequestFactory {
	HttpRequest getHttpRequest(Class<?> clazz, Method method, Object[] args) throws Exception;
}
