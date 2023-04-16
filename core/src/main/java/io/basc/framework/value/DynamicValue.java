package io.basc.framework.value;

import io.basc.framework.event.support.ObservableValue;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Registration;

public class DynamicValue<K> extends ObservableValue<Value> implements AutoCloseable {
	private final K key;
	private final ValueFactory<K> factory;
	private final Registration registration;

	public DynamicValue(K key, ValueFactory<K> factory) {
		this.key = key;
		this.factory = factory;
		set(factory.get(key));
		registration = factory.getKeyEventRegistry().registerListener((event) -> {
			if (!event.getSource().anyMatch((changeKey) -> ObjectUtils.equals(key, changeKey))) {
				return;
			}
			refresh();
		});
	}

	public void refresh() {
		set(factory.get(key));
	}

	public K getKey() {
		return key;
	}

	public ValueFactory<K> getFactory() {
		return factory;
	}

	@Override
	public void close() {
		registration.unregister();
	}
}
