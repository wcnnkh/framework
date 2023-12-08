package io.basc.framework.value;

import io.basc.framework.event.EventRegistry;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.util.element.Elements;

public interface DynamicValueFactory<K> extends ValueFactory<K>, EventRegistry<ObservableEvent<Elements<K>>> {

	default DynamicValue<K> getObservable(K key) {
		return new DynamicValue<K>(key, this);
	}
}
