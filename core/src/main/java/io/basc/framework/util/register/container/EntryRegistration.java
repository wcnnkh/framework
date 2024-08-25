package io.basc.framework.util.register.container;

import java.util.Map.Entry;

import io.basc.framework.util.register.KeyValueRegistration;
import io.basc.framework.util.register.Registration;

public interface EntryRegistration<K, V> extends KeyValueRegistration<K, V>, ElementRegistration<V>, Entry<K, V> {
	@Override
	default EntryRegistration<K, V> and(Registration registration) {
		if (registration == null || registration == EMPTY) {
			return this;
		}

		return new CombinableEntryRegistration<>(this, registration);
	}
}
