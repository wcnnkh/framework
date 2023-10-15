package io.basc.framework.mapper;

import io.basc.framework.util.element.Elements;

public class MergedSetter extends MergedParameterDescriptor<Setter> implements Setter {

	public MergedSetter(String name, Elements<? extends Setter> elements) {
		super(name, elements);
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
}
