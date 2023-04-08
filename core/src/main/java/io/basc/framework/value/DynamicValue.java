package io.basc.framework.value;

import io.basc.framework.event.support.ObservableValue;
import io.basc.framework.util.Registration;

public class DynamicValue<K> extends ObservableValue<Value> implements AutoCloseable {
	private final K key;
	private final ValueFactory<? super K> valueFactory;
	private final Registration registration;

	public DynamicValue(K key, ValueFactory<? super K> valueFactory) {
		this.key = key;
		this.valueFactory = valueFactory;
		this.registration = valueFactory.registerListener(key, (e) -> set(valueFactory.get(key)));
	}

	@Override
	public void close() {
		registration.unregister();
	}

	@Override
	public Value orElse(Value other) {
		Value value = super.orElse(null);
		if (value == null) {
			value = valueFactory.get(key);
		}
		return value == null ? Value.EMPTY : value;
	}
}
