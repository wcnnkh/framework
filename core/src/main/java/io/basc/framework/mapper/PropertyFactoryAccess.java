package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.value.EditablePropertyFactory;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class PropertyFactoryAccess implements ObjectAccess {
	private final PropertyFactory propertyFactory;

	public PropertyFactoryAccess(PropertyFactory propertyFactory) {
		this.propertyFactory = propertyFactory;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return TypeDescriptor.valueOf(PropertyFactory.class);
	}

	@Override
	public Elements<String> keys() {
		if (propertyFactory == null) {
			return Elements.empty();
		}
		return propertyFactory.keys();
	}

	@Override
	public Parameter get(String name) {
		if (propertyFactory == null) {
			return null;
		}

		Value value = propertyFactory.get(name);
		return value == null ? null : new Parameter(name, value);
	}

	@Override
	public void set(Parameter parameter) {
		if (propertyFactory == null || !(propertyFactory instanceof EditablePropertyFactory)) {
			throw new UnsupportedOperationException(String.valueOf(parameter));
		}

		((EditablePropertyFactory) propertyFactory).put(parameter.getName(), parameter);
	}

}
