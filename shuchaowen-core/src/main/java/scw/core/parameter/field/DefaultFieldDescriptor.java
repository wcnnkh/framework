package scw.core.parameter.field;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import scw.core.parameter.AbstractParameterDescriptor;

public class DefaultFieldDescriptor extends AbstractParameterDescriptor implements FieldDescriptor {
	private final Field field;

	public DefaultFieldDescriptor(Field field) {
		this.field = field;
	}

	public AnnotatedElement getAnnotatedElement() {
		return field;
	}

	public String getName() {
		return field.getName();
	}

	public final Class<?> getType() {
		return field.getType();
	}

	public final Type getGenericType() {
		return field.getGenericType();
	}

	public final Field getField() {
		return field;
	}
}
