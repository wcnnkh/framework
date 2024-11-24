package io.basc.framework.mapper.stereotype;

import io.basc.framework.core.execution.Getter;
import io.basc.framework.core.execution.Setter;
import io.basc.framework.util.ItemWrapper;

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
