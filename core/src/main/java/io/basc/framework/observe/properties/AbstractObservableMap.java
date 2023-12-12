package io.basc.framework.observe.properties;

import io.basc.framework.observe.Observer;

public abstract class AbstractObservableMap<K, V> extends Observer<PropertyChangeEvent<K, V>>
		implements ObservableMap<K, V> {
}
