package io.basc.framework.observe.properties;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.core.convert.Any;
import io.basc.framework.core.convert.lang.ValueFactory;

public class ValueFactories<K, F extends ValueFactory<K>> extends ConfigurableServices<F> implements ValueFactory<K> {
	@Override
	public Any get(K key) {
		for (F factory : getServices()) {
			if (factory == null || factory == this) {
				continue;
			}

			Any value = factory.get(key);
			if (value != null && value.isPresent()) {
				return value;
			}
		}
		return Any.EMPTY;
	}
}
