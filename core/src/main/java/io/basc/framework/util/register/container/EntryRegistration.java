package io.basc.framework.util.register.container;

import java.util.Map.Entry;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.register.KeyValueRegistration;

public interface EntryRegistration<K, V> extends KeyValueRegistration<K, V>, Entry<K, V> {

	@Override
	default EntryRegistration<K, V> and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}

		return new StandardEntryRegistrationWrapper<>(this, Elements.singleton(registration));
	}
}
