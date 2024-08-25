package io.basc.framework.util.register.container;

import io.basc.framework.util.register.Registration;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class AtomicEntryRegistration<K, V> extends AtomicElementRegistration<V> implements EntryRegistration<K, V> {
	private final K key;

	public AtomicEntryRegistration(K key, V value) {
		super(value);
		this.key = key;
	}

	protected AtomicEntryRegistration(AtomicEntryRegistration<K, V> entryRegistration) {
		this(entryRegistration, entryRegistration.key);
	}

	private AtomicEntryRegistration(AtomicElementRegistration<V> context, K key) {
		super(context);
		this.key = key;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public AtomicEntryRegistration<K, V> combine(@NonNull Registration registration) {
		return new AtomicEntryRegistration<>(super.combine(registration), this.key);
	}
}
