package io.basc.framework.value;

import io.basc.framework.event.BroadcastEventRegistry;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.util.Elements;

public interface DynamicValueFactory<K> extends ValueFactory<K> {

	default DynamicValue<K> getObservable(K key) {
		return new DynamicValue<K>(key, this);
	}

	BroadcastEventRegistry<ChangeEvent<Elements<K>>> getKeyEventRegistry();
}
