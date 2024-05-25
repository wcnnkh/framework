package io.basc.framework.mapper.entity;

import io.basc.framework.execution.Getter;
import io.basc.framework.execution.Setter;
import io.basc.framework.mapper.property.ItemWrapper;

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
