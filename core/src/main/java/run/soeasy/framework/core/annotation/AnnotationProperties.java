package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.transform.property.TypedProperties;
import run.soeasy.framework.core.type.ReflectionUtils;

public interface AnnotationProperties<A extends Annotation> extends TypedProperties, InvocationHandler {
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
			TypedValue typedValue = get(method.getName());
			return typedValue == null ? null
					: typedValue.map(TypeDescriptor.forMethodReturnType(method), Converter.assignable()).get();
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
