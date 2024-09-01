package io.basc.framework.util.register;

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
