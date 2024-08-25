package io.basc.framework.observe.properties;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.event.batch.BatchEventListener;
import io.basc.framework.util.register.Registration;
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
