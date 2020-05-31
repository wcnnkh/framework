package scw.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.reflect.ReflectionUtils;
import scw.lang.NestedExceptionUtils;

public abstract class InstanceInvoker implements MethodInvoker {

	public Class<?> getTargetClass() {
		return getMethod().getDeclaringClass();
	}

	protected abstract Object getInstance();

	public Object invoke(Object... args) throws Throwable {
		Method method = getMethod();
		ReflectionUtils.makeAccessible(method);
		try {
			return method.invoke(
					Modifier.isStatic(method.getModifiers()) ? null
							: getInstance(), args);
		} catch (Throwable e) {
			throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
		}
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
