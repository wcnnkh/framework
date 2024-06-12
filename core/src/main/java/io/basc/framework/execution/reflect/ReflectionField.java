package io.basc.framework.execution.reflect;

import java.io.Serializable;
import java.lang.reflect.Field;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.util.element.Elements;
import lombok.NonNull;

public abstract class ReflectionField extends ReflectionMember<Field> implements ParameterDescriptor, Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final Class<?> declaringClass;

	public ReflectionField(@NonNull Field member) {
		super(member);
		this.name = member.getName();
		this.declaringClass = member.getDeclaringClass();
	}

	@Override
	public Field getMember() {
		Field field = super.getMember();
		if (field == null) {
			synchronized (this) {
				field = super.getMember();
				if (field == null) {
					field = ReflectionUtils.getField(declaringClass, name);
				}
			}
		}
		return field;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	@Override
	public Elements<TypeDescriptor> getExceptionTypeDescriptors() {
		return Elements.empty();
	}

	private volatile MergedAnnotations annotations;

	@Override
	public MergedAnnotations getAnnotations() {
		if (annotations == null) {
			synchronized (this) {
				if (annotations == null) {
					annotations = MergedAnnotations.from(getMember());
				}
			}
		}
		return annotations;
	}

	private volatile TypeDescriptor typeDescriptor;

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					typeDescriptor = new TypeDescriptor(getMember());
				}
			}
		}
		return typeDescriptor;
	}
}
