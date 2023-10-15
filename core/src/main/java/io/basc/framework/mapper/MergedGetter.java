package io.basc.framework.mapper;

import io.basc.framework.util.element.Elements;

public class MergedGetter extends MergedParameterDescriptor<Getter> implements Getter {

	public MergedGetter(MergedParameterDescriptor<Getter> mergedParameterDescriptor) {
		super(mergedParameterDescriptor);
	}

	public MergedGetter(String name, Elements<? extends Getter> elements) {
		super(name, elements);
	}

	@Override
	public Object get(Object source) {
		return getMaster().get(source);
	}

	@Override
	public MergedGetter rename(String name) {
		MergedParameterDescriptor<Getter> mergedParameterDescriptor = super.rename(name);
		return new MergedGetter(mergedParameterDescriptor);
	}
}
