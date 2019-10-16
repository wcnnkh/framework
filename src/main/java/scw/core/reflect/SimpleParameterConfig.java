package scw.core.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import scw.core.parameter.ContainAnnotationParameterConfig;
import scw.core.utils.ArrayUtils;

public class SimpleParameterConfig implements ContainAnnotationParameterConfig {
	private final String name;
	private final Annotation[] annotations;
	private final Class<?> type;
	private final Type genericType;

	public SimpleParameterConfig(String name, Annotation[] annotations, Class<?> type,
			Type genericType) {
		this.name = name;
		this.annotations = annotations;
		this.type = type;
		this.genericType = genericType;
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
}
