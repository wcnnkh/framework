package io.basc.framework.util.register.container;

import java.util.Map.Entry;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.register.KeyValueRegistration;
import lombok.NonNull;

public interface EntryRegistration<K, V> extends KeyValueRegistration<K, V>, Entry<K, V> {

	public static class StandardEntryRegistrationWrapper<K, V, W extends EntryRegistration<K, V>>
			extends StandardKeyValueRegistrationWrapper<K, V, W> implements EntryRegistrationWrapper<K, V, W> {

		public StandardEntryRegistrationWrapper(@NonNull W source,
				@NonNull Elements<Registration> relatedRegistrations) {
			super(source, relatedRegistrations);
		}

		protected StandardEntryRegistrationWrapper(StandardKeyValueRegistrationWrapper<K, V, W> context) {
			super(context);
		}

		@Override
		public StandardEntryRegistrationWrapper<K, V, W> and(@NonNull Registration registration) {
			return new StandardEntryRegistrationWrapper<>(super.and(registration));
		}
	}

	public static interface EntryRegistrationWrapper<K, V, W extends EntryRegistration<K, V>>
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

	@Override
	default EntryRegistration<K, V> and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}

		return new StandardEntryRegistrationWrapper<>(this, Elements.singleton(registration));
	}
}
