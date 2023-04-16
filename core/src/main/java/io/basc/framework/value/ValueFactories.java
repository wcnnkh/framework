package io.basc.framework.value;

import io.basc.framework.factory.ConfigurableServices;

public class ValueFactories<K, F extends ValueFactory<K>> extends ConfigurableServices<F> implements ValueFactory<K> {

	@Override
	public Value get(K key) {
		for (F factory : this) {
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
