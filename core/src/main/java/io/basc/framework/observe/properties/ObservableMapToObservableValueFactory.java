package io.basc.framework.observe.properties;

import io.basc.framework.convert.lang.ObjectValue;
import io.basc.framework.util.actor.EventRegistrationException;
import io.basc.framework.util.actor.batch.BatchEventListener;
import io.basc.framework.util.register.Registration;
import lombok.Data;

@Data
class ObservableMapToObservableValueFactory<K> implements ObservableValueFactory<K> {
	private final ObservableMap<K, ObjectValue> observableMap;

	@Override
	public ObjectValue get(K key) {
		return observableMap.get(key);
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<PropertyChangeEvent<K, ObjectValue>> batchEventListener)
			throws EventRegistrationException {
		return observableMap.registerBatchListener(batchEventListener);
	}

}
