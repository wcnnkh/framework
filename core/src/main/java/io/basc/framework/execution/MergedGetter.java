package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.util.element.Elements;

public class MergedGetter extends MergedParameterDescriptor<Getter> implements Getter {

	public MergedGetter(MergedParameterDescriptor<Getter> mergedParameterDescriptor) {
		super(mergedParameterDescriptor);
	}

	public MergedGetter(Elements<? extends Getter> elements) {
		super(elements);
	}

	@Override
	public Object get(Object source) throws Throwable {
		return getMaster().get(source);
	}

	@Override
	public MergedGetter rename(String name) {
		MergedParameterDescriptor<Getter> mergedParameterDescriptor = super.rename(name);
		return new MergedGetter(mergedParameterDescriptor);
	}

	@Override
	public Elements<TypeDescriptor> getExceptionTypeDescriptors() {
		return getMaster().getExceptionTypeDescriptors();
	}

	@Override
	public TypeDescriptor getDeclaringTypeDescriptor() {
		return getMaster().getDeclaringTypeDescriptor();
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return getMaster().getAnnotations();
	}
}
