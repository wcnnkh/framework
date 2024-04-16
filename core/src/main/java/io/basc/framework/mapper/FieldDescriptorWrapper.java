package io.basc.framework.mapper;

import io.basc.framework.execution.Getter;
import io.basc.framework.execution.Setter;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.element.Elements;

public class FieldDescriptorWrapper<W extends FieldDescriptor> extends Wrapper<W> implements FieldDescriptor {

	public FieldDescriptorWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public String getName() {
		return wrappedTarget.getName();
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
	public Elements<String> getAliasNames() {
		return wrappedTarget.getAliasNames();
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
