package io.basc.framework.value.observe.support;

import java.util.concurrent.atomic.AtomicReference;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.value.ValueChangeEvent;

public class DefaultObservable<T> extends AbstractObservable<T> {
	private final AtomicReference<T> valueReference = new AtomicReference<>();

	@Override
	public T orElse(T other) {
		T value = this.valueReference.get();
		return value == null ? other : value;
	}

	private void publishEvent(T oldValue, T newValue) {
		if (oldValue == newValue) {
			return;
		}

		ChangeType changeType;
		if (oldValue == null) {
			changeType = ChangeType.CREATE;
		} else if (newValue == null) {
			changeType = ChangeType.DELETE;
		} else {
			changeType = ChangeType.UPDATE;
		}
		ValueChangeEvent<T> changeEvent = new ValueChangeEvent<T>(changeType, oldValue, newValue);
		publishEvent(changeEvent);
	}

	public T set(T newValue) {
		T oldValue = valueReference.getAndSet(newValue);
		publishEvent(oldValue, newValue);
		return oldValue;
	}

	public boolean set(T oldValue, T newValue) {
		if (valueReference.compareAndSet(oldValue, newValue)) {
			publishEvent(oldValue, newValue);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return valueReference.toString();
	}
}
