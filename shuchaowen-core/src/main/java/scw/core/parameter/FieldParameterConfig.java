package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import scw.core.annotation.ParameterName;
import scw.core.utils.StringUtils;

public class FieldParameterConfig implements ParameterConfig {
	private final Field field;

	public FieldParameterConfig(Field field) {
		this.field = field;
	}

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return field.getAnnotation(type);
	}

	public String getName() {
		ParameterName parameterName = getAnnotation(ParameterName.class);
		if (parameterName != null
				&& StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
		return field.getName();
	}

	public final Class<?> getType() {
		return field.getType();
	}

	public final Type getGenericType() {
		return field.getGenericType();
	}

	public final Field getField(){
		return field;
	}
}
