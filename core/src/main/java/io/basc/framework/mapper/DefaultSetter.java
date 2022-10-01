package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.parameter.DefaultParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.value.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class DefaultSetter extends AbstractFieldDescriptor implements Setter {
	private final String name;
	private final boolean nullable;
	private ParameterDescriptor setterParameterDescriptor;

	public DefaultSetter(Class<?> declaringClass, String name, Field field, Method method) {
		super(declaringClass, field, method);
		this.name = name;

		if (method != null) {
			Annotation[][] annotations = method.getParameterAnnotations();
			if (!ArrayUtils.isEmpty(annotations)) {
				setterParameterDescriptor = new DefaultParameterDescriptor(name, annotations[0],
						method.getParameterTypes()[0], method.getGenericParameterTypes()[0]);
			}
		}

		if (setterParameterDescriptor == null) {
			this.nullable = AnnotatedElementUtils.isNullable(this);
		} else {
			this.nullable = AnnotatedElementUtils.isNullable(setterParameterDescriptor,
					() -> AnnotatedElementUtils.isNullable(DefaultSetter.this));
		}
	}

	public String getName() {
		return name;
	}

	public boolean isNullable() {
		return nullable;
	}

	public Class<?> getType() {
		if (setterParameterDescriptor != null) {
			return setterParameterDescriptor.getType();
		}

		Field field = getField();
		if (field != null) {
			return field.getType();
		}
		throw createNotSupportException();
	}

	public Type getGenericType() {
		if (setterParameterDescriptor != null) {
			return setterParameterDescriptor.getGenericType();
		}

		Field field = getField();
		if (field != null) {
			return field.getGenericType();
		}
		throw createNotSupportException();
	}

	@Override
	public void set(Object instance, Object value) {
		if (value != null && value instanceof Value && getType() != Value.class
				&& !ClassUtils.isAssignableValue(getType(), value)) {
			super.set(instance, ((Value) value).getAsObject(new TypeDescriptor(this)));
			return;
		}
		super.set(instance, value);
	}
}
