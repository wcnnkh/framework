package io.basc.framework.observe.properties;

import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.convert.lang.ValueFactory;
import io.basc.framework.util.observe_old.Observable;

public interface ObservableValueFactory<K> extends ValueFactory<K>, Observable<PropertyChangeEvent<K, ValueWrapper>> {
	default DynamicValue<K> getDynamicValue(K key) {
		return new DynamicValue<K>(key, this);
	}
}
