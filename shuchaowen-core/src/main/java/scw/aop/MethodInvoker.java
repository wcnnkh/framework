package scw.aop;

import java.lang.reflect.Method;

import scw.lang.NestedExceptionUtils;

public abstract class MethodInvoker implements Invoker {

	public abstract Method getMethod();

	protected abstract Object getInstance();

	public Object invoke(Object... args) throws Throwable {
		try {
			return getMethod().invoke(getInstance(), args);
		} catch (Throwable e) {
			throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
		}
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
