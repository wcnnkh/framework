package scw.orm;

import scw.mapper.Field;

public class PropertyWrapper<T extends Property> extends PropertyDescriptorWrapper<T> implements Property{

	public PropertyWrapper(T wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Field getField() {
		return wrappedTarget.getField();
	}
	
}
