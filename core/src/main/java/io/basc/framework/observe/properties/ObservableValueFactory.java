package io.basc.framework.observe.properties;

import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.lang.ValueFactory;
import io.basc.framework.util.observe_old.Observable;

public interface ObservableValueFactory<K> extends ValueFactory<K>, Observable<PropertyChangeEvent<K, Value>> {
	default DynamicValue<K> getDynamicValue(K key) {
		return new DynamicValue<K>(key, this);
	}
}
