package scw.core.reflect;

import java.io.Serializable;

import scw.core.reflect.Field;

public class FieldContext implements Serializable {
	private static final long serialVersionUID = 1L;
	private final FieldContext parentContext;
	private final Field field;
	private final Class<?> declaringClass;

	public FieldContext(FieldContext parentContext, Field field,
			Class<?> declaringClass) {
		this.parentContext = parentContext;
		this.field = field;
		this.declaringClass = declaringClass;
	}

	public FieldContext getParentContext() {
		return parentContext;
	}

	public Field getField() {
		return field;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}
}
