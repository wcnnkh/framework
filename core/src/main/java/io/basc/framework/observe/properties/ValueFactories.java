package io.basc.framework.observe.properties;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;

public class ValueFactories<K, F extends ValueFactory<K>> extends ConfigurableServices<F> implements ValueFactory<K> {
	@Override
	public Value get(K key) {
		for (F factory : getServices()) {
			if (factory == null || factory == this) {
				continue;
			}

			Value value = factory.get(key);
			if (value != null && value.isPresent()) {
				return value;
			}
		}
		return Value.EMPTY;
	}
}
