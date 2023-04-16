package io.basc.framework.mapper;

import io.basc.framework.util.Elements;
import io.basc.framework.value.EditablePropertyFactory;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class PropertyFactoryAccess<E extends Throwable> implements ObjectAccess<E> {
	private final PropertyFactory propertyFactory;

	public PropertyFactoryAccess(PropertyFactory propertyFactory) {
		this.propertyFactory = propertyFactory;
	}

	@Override
	public Elements<String> keys() throws E {
		if (propertyFactory == null) {
			return Elements.empty();
		}
		return propertyFactory.keys();
	}

	@Override
	public Parameter get(String name) throws E {
		if (propertyFactory == null) {
			return null;
		}

		Value value = propertyFactory.get(name);
		return value == null ? null : new Parameter(name, value);
	}

	@Override
	public void set(Parameter parameter) throws E {
		if (propertyFactory == null || !(propertyFactory instanceof EditablePropertyFactory)) {
			throw new UnsupportedOperationException(String.valueOf(parameter));
		}

		((EditablePropertyFactory) propertyFactory).put(parameter.getName(), parameter);
	}

}
