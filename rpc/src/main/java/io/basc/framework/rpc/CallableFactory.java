package io.basc.framework.rpc;

import io.basc.framework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public interface CallableFactory {
	@Nullable
	Callable<Object> getCallable(Class<?> clazz, Method method, Object[] args);
}
