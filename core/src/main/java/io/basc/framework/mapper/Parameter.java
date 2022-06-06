package io.basc.framework.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.EmptyValue;
import io.basc.framework.value.Value;

public class Parameter implements ParameterDescriptor, Supplier<Object> {
	/**
	 * 无效的
	 */
	public static final Parameter INVALID = new Parameter(null);

	private final String name;
	private final Value value;

	public Parameter(String name) {
		this(name, null, null);
	}

	public Parameter(String name, Object value) {
		this(name, new AnyValue(value));
	}

	public Parameter(String name, Object value, TypeDescriptor valueTypeDescriptor) {
		this(name, new AnyValue(value, valueTypeDescriptor));
	}

	public Parameter(String name, Value value) {
		this.name = name;
		this.value = value;
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

	public TypeDescriptor getTypeDescriptor() {
		return getValue().getTypeDescriptor();
	}

	@Override
	public Type getGenericType() {
		return getTypeDescriptor().getResolvableType().getType();
	}

	@Nullable
	public final Value getValue() {
		return this.value == null ? EmptyValue.INSTANCE : this.value;
	}

	@Override
	public final Object get() {
		return getValue().get();
	}

	@Override
	public Parameter rename(String name) {
		Assert.requiredArgument(StringUtils.isNotEmpty(name), "name");
		return new Parameter(name, this.value);
	}

	public Parameter update(Value value) {
		return new Parameter(this.name, value);
	}

	public Parameter update(Object value) {
		return update(new AnyValue(value));
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
