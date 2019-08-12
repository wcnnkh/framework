package scw.beans.rpc.http;

import java.lang.reflect.Method;

import scw.net.http.HttpRequest;

public interface RpcRequestFactory {
	HttpRequest createHttpRequest(Class<?> clazz, Method method, String host, Object[] args) throws Exception;
}
