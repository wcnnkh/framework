package io.basc.framework.util.register.container;

import io.basc.framework.util.Registration;
import io.basc.framework.util.register.KeyValueRegistrationWrapper;

public interface EntryRegistrationWrapper<K, V, W extends EntryRegistration<K, V>>
		extends EntryRegistration<K, V>, KeyValueRegistrationWrapper<K, V, W> {

	@Override
	default V getValue() {
		return getSource().getValue();
	}

	@Override
	default K getKey() {
		return getSource().getKey();
	}

	@Override
	default V setValue(V value) {
		return getSource().setValue(value);
	}

	@Override
	default EntryRegistration<K, V> and(Registration registration) {
		return EntryRegistration.super.and(registration);
	}

}
