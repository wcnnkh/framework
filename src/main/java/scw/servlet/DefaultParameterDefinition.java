package scw.servlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import scw.core.reflect.EmptyInvocationHandler;
import scw.core.utils.ArrayUtils;

public class DefaultParameterDefinition implements ParameterDefinition {
	public static final RequestParameterDefinition REQUEST_PARAMETER_DEFINITION = (RequestParameterDefinition) Proxy
			.newProxyInstance(
					DefaultParameterDefinition.class.getClassLoader(),
					new Class<?>[] { RequestParameterDefinition.class },
					new EmptyInvocationHandler());
	public static final ResponseParameterDefinition RESPONSE_PARAMETER_DEFINITION = (ResponseParameterDefinition) Proxy
			.newProxyInstance(
					DefaultParameterDefinition.class.getClassLoader(),
					new Class<?>[] { ResponseParameterDefinition.class },
					new EmptyInvocationHandler());

	public static interface ResponseParameterDefinition extends
			ParameterDefinition {
	}

	public static interface RequestParameterDefinition extends
			ParameterDefinition {
	}

	private final String name;
	private final Annotation[] annotations;
	private final Class<?> type;
	private final Type genericType;
	private final int index;
	private final int parameterCount;

	public DefaultParameterDefinition(int parameterCount, String name,
			Annotation[] annotations, Class<?> type, Type genericType, int index) {
		this.name = name;
		this.annotations = annotations;
		this.type = type;
		this.genericType = genericType;
		this.index = index;
		this.parameterCount = parameterCount;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<T> type) {
		if (ArrayUtils.isEmpty(annotations)) {
			return null;
		}

		for (Annotation a : annotations) {
			if (a == null) {
				continue;
			}

			if (type.isInstance(a)) {
				return (T) a;
			}
		}
		return null;
	}

	public Class<?> getType() {
		return type;
	}

	public Type getGenericType() {
		return genericType;
	}

	public int getIndex() {
		return index;
	}

	public int getParameterCount() {
		return parameterCount;
	}
}
