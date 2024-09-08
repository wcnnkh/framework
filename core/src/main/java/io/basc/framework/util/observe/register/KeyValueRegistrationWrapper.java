package io.basc.framework.util.observe.register;

import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.RegistrationWrapper;

public interface KeyValueRegistrationWrapper<K, V, W extends KeyValueRegistration<K, V>>
		extends RegistrationWrapper<W>, KeyValueRegistration<K, V> {

	@Override
	default KeyValueRegistration<K, V> and(Registration registration) {
		return getSource().and(registration);
	}

	@Override
	default K getKey() {
		return getSource().getKey();
	}

	@Override
	default V getValue() {
		return getSource().getValue();
	}
}
