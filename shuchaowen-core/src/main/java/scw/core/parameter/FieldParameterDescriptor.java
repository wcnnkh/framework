package scw.core.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import scw.core.annotation.ParameterName;
import scw.core.utils.StringUtils;

public class FieldParameterDescriptor implements ParameterDescriptor {
	private final Field field;

	public FieldParameterDescriptor(Field field) {
		this.field = field;
	}
	
	public AnnotatedElement getAnnotatedElement() {
		return field;
	}

	public String getName() {
		ParameterName parameterName = getAnnotatedElement().getAnnotation(ParameterName.class);
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
