package io.basc.framework.observe.properties;

import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.util.actor.EventRegistrationException;
import io.basc.framework.util.actor.batch.BatchEventListener;
import io.basc.framework.util.register.Registration;
import lombok.Data;

@Data
class ObservableMapToObservableValueFactory<K> implements ObservableValueFactory<K> {
	private final ObservableMap<K, ValueWrapper> observableMap;

	@Override
	public ValueWrapper get(K key) {
		return observableMap.get(key);
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<PropertyChangeEvent<K, ValueWrapper>> batchEventListener)
			throws EventRegistrationException {
		return observableMap.registerBatchListener(batchEventListener);
	}

}
