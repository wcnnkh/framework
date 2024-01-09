package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.util.element.Elements;

public class MergedSetter extends MergedParameterDescriptor<Setter> implements Setter {

	public MergedSetter(Elements<? extends Setter> elements) {
		super(elements);
	}

	public MergedSetter(MergedParameterDescriptor<Setter> mergedParameterDescriptor) {
		super(mergedParameterDescriptor);
	}

	@Override
	public MergedSetter rename(String name) {
		MergedParameterDescriptor<Setter> mergedParameterDescriptor = super.rename(name);
		return new MergedSetter(mergedParameterDescriptor);
	}

	@Override
	public void set(Object target, Object value) throws Throwable {
		getMaster().set(target, value);
	}

	@Override
	public Elements<TypeDescriptor> getExceptionTypeDescriptors() {
		return getMaster().getExceptionTypeDescriptors();
	}

	@Override
	public TypeDescriptor getDeclaringTypeDescriptor() {
		return getMaster().getReturnTypeDescriptor();
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return getMaster().getAnnotations();
	}
}
