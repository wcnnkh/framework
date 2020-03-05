package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import scw.core.annotation.ParameterName;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;

public class DefaultParameterConfig implements ParameterConfig {
	private final String name;
	private final Annotation[] annotations;
	private final Class<?> type;
	private final Type genericType;

	public DefaultParameterConfig(String name, Annotation[] annotations, Class<?> type,
			Type genericType) {
		this.name = name;
		this.annotations = annotations;
		this.type = type;
		this.genericType = genericType;
	}

	public String getName() {
		ParameterName parameterName = getAnnotation(ParameterName.class);
		if (parameterName != null
				&& StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
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
