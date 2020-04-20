package scw.async.filter;

import java.lang.reflect.Method;

public interface AsyncService {
	void service(Async async, Class<?> targetClass, Method method, Object[] args) throws Exception;
}
