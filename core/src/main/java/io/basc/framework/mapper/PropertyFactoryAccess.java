package io.basc.framework.mapper;

import java.util.Collections;
import java.util.Enumeration;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.value.ConfigurablePropertyFactory;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class PropertyFactoryAccess<E extends Throwable> implements ObjectAccess<E> {
	private final PropertyFactory propertyFactory;

	public PropertyFactoryAccess(PropertyFactory propertyFactory) {
		this.propertyFactory = propertyFactory;
	}

	@Override
	public Enumeration<String> keys() throws E {
		if (propertyFactory == null) {
			return Collections.emptyEnumeration();
		}

		return CollectionUtils.toEnumeration(propertyFactory.stream().iterator());
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
		if (propertyFactory == null || !(propertyFactory instanceof ConfigurablePropertyFactory)) {
			throw new UnsupportedOperationException(String.valueOf(parameter));
		}

		((ConfigurablePropertyFactory) propertyFactory).put(parameter.getName(), parameter);
	}

}
