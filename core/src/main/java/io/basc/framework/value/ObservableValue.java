package io.basc.framework.value;

import io.basc.framework.event.support.StandardObservable;
import io.basc.framework.util.Registration;

public class ObservableValue<K> extends StandardObservable<Value> implements AutoCloseable {
	private final K key;
	private final ValueFactory<? super K> valueFactory;
	private final Registration registration;

	public ObservableValue(K key, ValueFactory<? super K> valueFactory) {
		this.key = key;
		this.valueFactory = valueFactory;
		this.registration = valueFactory.registerListener(key, (e) -> set(valueFactory.get(key)));
	}

	@Override
	public void close() {
		registration.unregister();
	}

	@Override
	protected Value getValue() {
		Value value = super.getValue();
		return value == null ? valueFactory.get(key) : value;
	}
}
