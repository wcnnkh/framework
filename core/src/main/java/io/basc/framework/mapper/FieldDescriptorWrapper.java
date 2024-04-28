package io.basc.framework.mapper;

import io.basc.framework.execution.Getter;
import io.basc.framework.execution.Setter;

public class FieldDescriptorWrapper<W extends FieldDescriptor> extends ItemWrapper<W> implements FieldDescriptor {

	public FieldDescriptorWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Getter getter() {
		return wrappedTarget.getter();
	}

	@Override
	public Setter setter() {
		return wrappedTarget.setter();
	}

	@Override
	public boolean isSupportGetter() {
		return wrappedTarget.isSupportGetter();
	}

	@Override
	public boolean isSupportSetter() {
		return wrappedTarget.isSupportSetter();
	}

}
