package scw.rpc.http;

import java.lang.reflect.Method;

import scw.http.HttpRequestEntity;

public interface HttpRequestEntityFactory {
	HttpRequestEntity<?> getHttpRequestEntity(Class<?> clazz, Method method, Object[] args);
}
