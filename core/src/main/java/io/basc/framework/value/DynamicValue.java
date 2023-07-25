package io.basc.framework.value;

import io.basc.framework.event.observe.support.ObservableValue;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.registry.Registration;

public class DynamicValue<K> extends ObservableValue<Value> implements AutoCloseable {
	private final K key;
	private final DynamicValueFactory<K> valueFactory;
	private final Registration registration;

	public DynamicValue(K key, DynamicValueFactory<K> valueFactory) {
		this.key = key;
		this.valueFactory = valueFactory;
		set(valueFactory.get(key));
		registration = valueFactory.getKeyEventRegistry().registerListener((event) -> {
			for (K changeKey : event.getSource()) {
				if (ObjectUtils.equals(key, changeKey)) {
					refresh();
					break;
				}
			}
		});
	}

	public void refresh() {
		set(valueFactory.get(key));
	}

	public K getKey() {
		return key;
	}

	public DynamicValueFactory<K> getValueFactory() {
		return valueFactory;
	}

	@Override
	public void close() {
		registration.unregister();
	}
}
