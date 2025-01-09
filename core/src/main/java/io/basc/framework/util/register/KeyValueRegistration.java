package io.basc.framework.util.register;

import io.basc.framework.util.KeyValue;
import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.exchange.Registration;
import lombok.NonNull;

public interface KeyValueRegistration<K, V> extends PayloadRegistration<KeyValue<K, V>>, KeyValue<K, V> {

	public static class StandardKeyValueRegistrationWrapper<K, V, W extends KeyValueRegistration<K, V>> extends
			StandardPayloadRegistrationWrapper<KeyValue<K, V>, W> implements KeyValueRegistrationWrapper<K, V, W> {

		public StandardKeyValueRegistrationWrapper(@NonNull W source,
				@NonNull Elements<Registration> relatedRegistrations) {
			super(source, relatedRegistrations);
		}

		protected StandardKeyValueRegistrationWrapper(StandardRegistrationWrapper<W> context) {
			super(context);
		}

		@Override
		public StandardKeyValueRegistrationWrapper<K, V, W> and(Registration registration) {
			return new StandardKeyValueRegistrationWrapper<>(super.combine(registration));
		}

		@Override
		public KeyValue<K, V> getPayload() {
			return super.getPayload();
		}
	}

	public static interface KeyValueRegistrationWrapper<K, V, W extends KeyValueRegistration<K, V>>
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

	@Override
	K getKey();

	@Override
	V getValue();

	@Override
	default KeyValue<K, V> getPayload() {
		return KeyValue.of(getKey(), getValue());
	}

	@Override
	default KeyValueRegistration<K, V> and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}
		return new StandardKeyValueRegistrationWrapper<>(this, Elements.singleton(registration));
	}
}
