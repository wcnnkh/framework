package scw.mvc.rpc.http;

import java.lang.reflect.Method;

import scw.net.http.SimpleClientHttpRequest;

public interface HttpRpcRequestFactory {
	SimpleClientHttpRequest getHttpRequest(Class<?> clazz, Method method, Object[] args) throws Exception;
}
