package io.basc.framework.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import io.basc.framework.core.annotation.Annotations;

public class DefaultGetter extends AbstractFieldDescriptor implements Getter {
	private final String name;
	private final boolean nullable;

	public DefaultGetter(Class<?> sourceClass, String name, Field field, Method method) {
		super(sourceClass, field, method);
		this.name = name;
		this.nullable = Annotations.isNullable(this);
	}

	public String getName() {
		return name;
	}

	public boolean isNullable() {
		return nullable;
	}

	public Class<?> getType() {
		Method method = getMethod();
		if (method != null) {
			return method.getReturnType();
		}

		Field field = getField();
		if (field != null) {
			return field.getType();
		}
		throw createNotSupportException();
	}

	public Type getGenericType() {
		Method method = getMethod();
		if (method != null) {
			return method.getGenericReturnType();
		}

		Field field = getField();
		if (field != null) {
			return field.getGenericType();
		}
		throw createNotSupportException();
	}
}
