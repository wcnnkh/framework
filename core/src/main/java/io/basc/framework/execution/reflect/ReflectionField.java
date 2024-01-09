package io.basc.framework.execution.reflect;

import java.lang.reflect.Field;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;
import lombok.NonNull;

public abstract class ReflectionField extends ReflectionMember<Field> implements ParameterDescriptor {

	public ReflectionField(@NonNull Field member) {
		super(member);
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
