package run.soeasy.framework.core.exchange.container;

import java.util.function.BiPredicate;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Registration;

public interface KeyValueRegistration<K, V> extends PayloadRegistration<KeyValue<K, V>>, KeyValue<K, V> {
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
		return new KeyValueRegistrationWrapped<>(this, Elements.singleton(registration));
	}
}
