package scw.aop;

import java.lang.reflect.Method;

import scw.core.reflect.ReflectionUtils;
import scw.lang.NestedExceptionUtils;

public final class ReflectInvoker implements Invoker {
	private final Object obj;
	private final Method method;

	public ReflectInvoker(Object obj, Method method) {
		this.obj = obj;
		this.method = method;
		ReflectionUtils.setAccessibleMethod(method);
	}

	public Object invoke(Object... args) throws Throwable {
		try {
			return method.invoke(obj, args);
		} catch (Throwable e) {
			throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
		}
	}

	@Override
	public String toString() {
		return method.toString();
	}
}
