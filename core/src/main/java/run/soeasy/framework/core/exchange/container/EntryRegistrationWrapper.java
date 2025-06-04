package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.exchange.Registration;


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