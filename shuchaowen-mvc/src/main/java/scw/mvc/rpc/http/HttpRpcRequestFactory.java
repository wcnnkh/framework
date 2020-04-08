package scw.mvc.rpc.http;

import java.lang.reflect.Method;

import scw.net.client.http.ClientHttpRequest;

public interface HttpRpcRequestFactory {
	ClientHttpRequest getHttpRequest(Class<?> clazz, Method method, Object[] args) throws Exception;
}
