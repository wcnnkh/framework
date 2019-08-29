package scw.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import scw.core.utils.ArrayUtils;

public class SimpleParameterDefinition implements ParameterDefinition {
	private final String name;
	private final Annotation[] annotations;
	private final Class<?> type;
	private final Type genericType;
	private final int index;
	private final int parameterCount;

	public SimpleParameterDefinition(int parameterCount, String name, Annotation[] annotations, Class<?> type,
			Type genericType, int index) {
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
