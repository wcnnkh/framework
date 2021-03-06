package scw.rpc;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import scw.lang.Nullable;

public interface CallableFactory {
	@Nullable
	Callable<Object> getCallable(Class<?> clazz, Method method, Object[] args);
}
