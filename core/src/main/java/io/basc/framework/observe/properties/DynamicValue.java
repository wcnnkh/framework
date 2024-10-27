package io.basc.framework.observe.properties;

import io.basc.framework.convert.lang.ObjectValue;
import io.basc.framework.observe.value.AbstractObservableValue;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.EventRegistrationException;
import io.basc.framework.util.actor.batch.BatchEventListener;
import io.basc.framework.util.register.Registration;

public class DynamicValue<K> extends AbstractObservableValue<ObjectValue> implements AutoCloseable {
	private final K key;
	private final ObservableValueFactory<K> observableValueFactory;
	private volatile Registration registration;

	public DynamicValue(K key, ObservableValueFactory<K> observableValueFactory) {
		this.key = key;
		this.observableValueFactory = observableValueFactory;
	}

	@Override
	public ObjectValue orElse(ObjectValue other) {
		ObjectValue value = observableValueFactory.get(key);
		if (value == null || !value.isPresent()) {
			return other;
		}
		return value;
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<ChangeEvent> batchEventListener)
			throws EventRegistrationException {
		Registration registration = super.registerBatchListener(batchEventListener);
		refresh();
		return registration.and(() -> refresh());
	}

	private void refresh() {
		if (getListenerCount() == 0) {
			close();
		} else {
			if (registration == null) {
				synchronized (this) {
					if (registration == null) {
						registration = observableValueFactory.registerBatchListener((events) -> {
							events.forEach((event) -> {
								if (key.equals(event.getKey())) {
									publishEvent(new ChangeEvent(event.getSource(), event.getType()));
								}
							});
						});
					}
				}
			}
		}
	}

	@Override
	public void close() {
		if (registration != null) {
			synchronized (this) {
				if (registration != null) {
					registration.unregister();
					registration = null;
				}
			}
		}
	}
}
