package io.basc.framework.observe.properties;

import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.util.Registration;
import io.basc.framework.value.Value;
import lombok.Data;

@Data
class ObservableMapToObservableValueFactory<K> implements ObservableValueFactory<K> {
	private final ObservableMap<K, Value> observableMap;

	@Override
	public Value get(K key) {
		return observableMap.get(key);
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<PropertyChangeEvent<K, Value>> batchEventListener)
			throws EventRegistrationException {
		return observableMap.registerBatchListener(batchEventListener);
	}

}
