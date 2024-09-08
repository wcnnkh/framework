package io.basc.framework.observe.value;

import java.util.concurrent.atomic.AtomicReference;

import io.basc.framework.util.observe.event.ChangeEvent;

public class AtomicObservableValue<V> extends AbstractObservableValue<V> {
	private AtomicReference<V> valueReference = new AtomicReference<>();

	public V setValue(V value) {
		V oldValue = valueReference.getAndSet(value);
		modifyValue(oldValue, value, System.currentTimeMillis());
		return oldValue;
	}

	public boolean setValue(V oldValue, V newValue) {
		if (valueReference.compareAndSet(oldValue, newValue)) {
			modifyValue(oldValue, newValue, System.currentTimeMillis());
			return true;
		}
		return false;
	}

	/**
	 * 修改值
	 * 
	 * @param oldValue
	 * @param newValue
	 */
	protected void modifyValue(V oldValue, V newValue, long lastModified) {
		if (oldValue == newValue) {
			return;
		}

		getAtomicLastModified().set(lastModified);
		ChangeEvent<V> changeEvent = new ChangeEvent<V>(this, oldValue, newValue);
		publishEvent(changeEvent);
	}

	@Override
	public V orElse(V other) {
		V value = valueReference.get();
		return value == null ? other : value;
	}
}
