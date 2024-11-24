package io.basc.framework.core.execution;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.param.MergedParameterDescriptor;
import io.basc.framework.util.Elements;

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
	public void set(Object target, Object value) {
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
}
