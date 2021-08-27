package io.basc.framework.rpc.http;

import io.basc.framework.http.HttpRequestEntity;

import java.lang.reflect.Method;

public interface HttpRequestEntityFactory {
	HttpRequestEntity<?> getHttpRequestEntity(Class<?> clazz, Method method, Object[] args);
}
