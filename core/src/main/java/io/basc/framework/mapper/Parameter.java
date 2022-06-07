package io.basc.framework.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.AnyValue;

public class Parameter extends AnyValue implements ParameterDescriptor {
	private static final long serialVersionUID = 1L;

	/**
	 * 无效的
	 */
	public static final Parameter INVALID = new Parameter(null, null);

	private final String name;

	public Parameter(String name) {
		this(name, null);
	}

	public Parameter(String name, Object value) {
		this(name, value, null);
	}

	public Parameter(String name, Object value, TypeDescriptor typeDescriptor) {
		super(value, typeDescriptor);
		this.name = name;
	}

	protected Parameter(String name, AnyValue value) {
		super(value);
		this.name = name;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return getTypeDescriptor().getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getAnnotations() {
		return getTypeDescriptor().getAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return getTypeDescriptor().getDeclaredAnnotations();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getType() {
		return getTypeDescriptor().getType();
	}

	@Override
	public Type getGenericType() {
		return getTypeDescriptor().getResolvableType().getType();
	}

	@Override
	public Parameter rename(String name) {
		Assert.requiredArgument(StringUtils.isNotEmpty(name), "name");
		return new Parameter(name, this);
	}

	/**
	 * 是否是无效的字段
	 * 
	 * @return
	 */
	public boolean isInvalid() {
		return StringUtils.isEmpty(name);
	}
}
