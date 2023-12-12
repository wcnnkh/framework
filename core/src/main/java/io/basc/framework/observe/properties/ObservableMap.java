package io.basc.framework.observe.properties;

import java.util.Map;

import io.basc.framework.observe.Observable;
import io.basc.framework.observe.value.ObservableValue;

public interface ObservableMap<K, V> extends Map<K, V>, Observable<PropertyChangeEvent<K, V>> {

	default ObservableValue<Map<K, V>> asObservableValue() {
		return new ObservableMapToObservableValue<>(this);
	}
}
