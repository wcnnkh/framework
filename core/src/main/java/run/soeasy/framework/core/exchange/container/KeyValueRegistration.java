package run.soeasy.framework.core.exchange.container;

import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

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

	@RequiredArgsConstructor
	public static class DisposableKeyValueRegistration<K, V> extends AbstractLifecycleRegistration
			implements KeyValueRegistration<K, V> {
		private final K key;
		private final V value;
		@NonNull
		private final BiPredicate<? super K, ? super V> runnable;

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public boolean cancel(BooleanSupplier cancel) {
			return super.cancel(() -> {
				if (cancel.getAsBoolean()) {
					return runnable.test(key, value);
				}
				return false;
			});
		}
	}

	public static <K, V> KeyValueRegistration<K, V> of(K key, V value,
			@NonNull BiPredicate<? super K, ? super V> runnable) {
		return new DisposableKeyValueRegistration<>(key, value, runnable);
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
