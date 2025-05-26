package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import run.soeasy.framework.core.transform.property.PropertyMapping;
import run.soeasy.framework.core.type.ReflectionUtils;

public interface AnnotationPropertyMapping<A extends Annotation> extends PropertyMapping, InvocationHandler {
	Class<A> getType();

	@Override
	default Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (ReflectionUtils.isEqualsMethod(method)) {
			return equals(args[0]);
		}
		if (ReflectionUtils.isHashCodeMethod(method)) {
			return hashCode();
		}
		if (ReflectionUtils.isToStringMethod(method)) {
			return toString();
		}

		if (method.getName().equals("annotationType") && method.getParameterCount() == 0) {
			return getType();
		}

		if (hasKey(method.getName())) {
			return getAsObject(method.getName(), method.getReturnType(), () -> null);
		}
		throw new IllegalArgumentException(
				String.format("Method [%s] is unsupported for synthesized annotation type [%s]", method, getType()));
	}

	@SuppressWarnings("unchecked")
	default A synthesize() {
		Class<A> type = getType();
		ClassLoader classLoader = type.getClassLoader();
		return (A) Proxy.newProxyInstance(classLoader, new Class<?>[] { type, SynthesizedAnnotation.class }, this);
	}
}
