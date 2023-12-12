package io.basc.framework.observe.properties;

import io.basc.framework.observe.Observable;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;

public interface ObservableValueFactory<K> extends ValueFactory<K>, Observable<PropertyChangeEvent<K, Value>> {
	default DynamicValue<K> getDynamicValue(K key) {
		return new DynamicValue<K>(key, this);
	}
}
