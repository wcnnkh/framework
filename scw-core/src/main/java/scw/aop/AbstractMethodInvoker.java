package scw.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.reflect.MethodInvoker;
import scw.core.reflect.ReflectionUtils;
import scw.lang.NestedExceptionUtils;

public abstract class AbstractMethodInvoker implements MethodInvoker {
	private final Class<?> sourceClass;

	public AbstractMethodInvoker(Class<?> sourceClass) {
		this.sourceClass = sourceClass;
	}

	public Class<?> getSourceClass() {
		return sourceClass == null ? getMethod().getDeclaringClass() : sourceClass;
	}

	public Object invoke(Object... args) throws Throwable {
		Method method = getMethod();
		ReflectionUtils.makeAccessible(method);
		try {
			return method.invoke(Modifier.isStatic(method.getModifiers()) ? null : getInstance(), args);
		} catch (Throwable e) {
			throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
		}
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
