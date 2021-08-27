package io.basc.framework.core.reflect;

import java.lang.reflect.Field;

public final class SimpleFieldHolder implements FieldHolder {
	private final Class<?> declaringClass;
	private final Field field;

	public SimpleFieldHolder(Class<?> declaringClass, Field field) {
		this.declaringClass = declaringClass;
		this.field = field;
	}

	public java.lang.reflect.Field getField() {
		return field;
	};
	
	@Override
	public String toString() {
		return field.toString();
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}
}
