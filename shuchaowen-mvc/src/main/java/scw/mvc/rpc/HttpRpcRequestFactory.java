package scw.mvc.rpc;

import java.lang.reflect.Method;

import scw.net.http.client.ClientHttpRequest;

public interface HttpRpcRequestFactory {
	ClientHttpRequest getHttpRequest(Class<?> clazz, Method method, Object[] args) throws Exception;
}
