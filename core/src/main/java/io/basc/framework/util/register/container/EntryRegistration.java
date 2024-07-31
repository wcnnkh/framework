package io.basc.framework.util.register.container;

import java.util.Map.Entry;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.Registration;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class EntryRegistration<K, V> extends ElementRegistration<Entry<K, V>> implements Entry<K, V> {
	public EntryRegistration(@NonNull Entry<K, V> payload) {
		super(payload);
	}

	protected EntryRegistration(@NonNull ElementRegistration<Entry<K, V>> containerRegistration) {
		super(containerRegistration);
	}

	@Override
	public K getKey() {
		return getPayload().getKey();
	}

	@Override
	public V getValue() {
		return getPayload().getValue();
	}

	@Override
	public V setValue(V value) {
		return getPayload().setValue(value);
	}

	@Override
	public EntryRegistration<K, V> and(@NonNull Registration registration) {
		return new EntryRegistration<>(super.and(registration));
	}

	@Override
	public EntryRegistration<K, V> andAll(@NonNull Elements<? extends Registration> registrations) {
		return new EntryRegistration<>(super.andAll(registrations));
	}
}
