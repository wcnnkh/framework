package io.basc.framework.observe.value;

import io.basc.framework.observe.ChangeEvent;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.observe.VariableObserver;

public abstract class AbstractObservableValue<V> extends VariableObserver<ChangeEvent> implements ObservableValue<V> {

	protected void modifyLastModified(long oldValue, long newValue) {
		publishEvent(new ObservableEvent<>(this, oldValue, newValue));
	}

	public long setLastModified(long lastModified) {
		long oldLastModified = super.setLastModified(lastModified);
		modifyLastModified(oldLastModified, lastModified);
		return oldLastModified;
	}

	public boolean setLastModified(long oldValue, long newValue) {
		if (super.setLastModified(oldValue, newValue)) {
			modifyLastModified(oldValue, newValue);
			return true;
		}
		return false;
	}
}
