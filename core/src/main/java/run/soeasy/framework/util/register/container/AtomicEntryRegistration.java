package run.soeasy.framework.util.register.container;

import java.util.concurrent.atomic.AtomicReference;

import lombok.Getter;

@Getter
public class AtomicEntryRegistration<K, V> extends AbstractEntryRegistration<K, V> implements EntryRegistration<K, V> {
	private final K key;
	private final AtomicReference<V> valueReference;

	public AtomicEntryRegistration(K key, V value) {
		this.key = key;
		this.valueReference = new AtomicReference<V>(value);
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return valueReference.get();
	}

	@Override
	public V setValue(V value) {
		return valueReference.getAndSet(value);
	}
}
