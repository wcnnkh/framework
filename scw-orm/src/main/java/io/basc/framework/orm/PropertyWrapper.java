package io.basc.framework.orm;

import io.basc.framework.mapper.Field;

public class PropertyWrapper<T extends Property> extends PropertyDescriptorWrapper<T> implements Property{

	public PropertyWrapper(T wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Field getField() {
		return wrappedTarget.getField();
	}
	
}
