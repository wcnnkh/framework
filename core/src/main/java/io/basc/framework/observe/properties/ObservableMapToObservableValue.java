package io.basc.framework.observe.properties;

import java.util.Map;

import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.observe.ChangeEvent;
import io.basc.framework.observe.value.ObservableValue;
import io.basc.framework.util.Registration;

class ObservableMapToObservableValue<K, V> implements ObservableValue<Map<K, V>> {
	private final ObservableMap<K, V> observableMap;

	public ObservableMapToObservableValue(ObservableMap<K, V> observableMap) {
		this.observableMap = observableMap;
	}

	@Override
	public Map<K, V> orElse(Map<K, V> other) {
		return observableMap;
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<ChangeEvent> batchEventListener)
			throws EventRegistrationException {
		return observableMap.registerBatchListener((events) -> batchEventListener.onEvent(events.map((e) -> e)));
	}
}