package io.basc.framework.orm;

import io.basc.framework.util.Wrapper;

public class PropertyDescriptorWrapper<T extends PropertyDescriptor> extends
		Wrapper<T> implements PropertyDescriptor {

	public PropertyDescriptorWrapper(T wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public String getName() {
		return wrappedTarget.getName();
	}

	@Override
	public boolean isPrimaryKey() {
		return wrappedTarget.isPrimaryKey();
	}

	@Override
	public boolean isNullable() {
		return wrappedTarget.isNullable();
	}
}
