package io.basc.framework.orm.repository;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.env.BascObject;
import io.basc.framework.lang.Nullable;

public class RepositoryColumn extends BascObject implements Serializable, ParameterDescriptor {
	public static final RepositoryColumn EMPTY = new RepositoryColumn(null, null);

	private static final long serialVersionUID = 1L;
	private final String name;
	private final Object value;
	private final TypeDescriptor valueTypeDescriptor;

	public RepositoryColumn(String name, Object value) {
		this(name, value, null);
	}

	public RepositoryColumn(String name, Object value, @Nullable TypeDescriptor valueTypeDescriptor) {
		this.name = name;
		this.value = value;
		this.valueTypeDescriptor = valueTypeDescriptor;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public TypeDescriptor getValueTypeDescriptor() {
		return valueTypeDescriptor == null ? TypeDescriptor.forObject(value) : valueTypeDescriptor;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return getValueTypeDescriptor().getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getAnnotations() {
		return getValueTypeDescriptor().getAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return getValueTypeDescriptor().getDeclaredAnnotations();
	}

	@Override
	public Class<?> getType() {
		return getValueTypeDescriptor().getType();
	}

	@Override
	public Type getGenericType() {
		return getValueTypeDescriptor().getResolvableType().getType();
	}

	@Override
	public ParameterDescriptor rename(String name) {
		return new RepositoryColumn(name, this.value, this.valueTypeDescriptor);
	}
}
