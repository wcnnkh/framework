package io.basc.framework.observe.value;

import io.basc.framework.observe.Observer;
import io.basc.framework.observe.mode.ChangeEvent;

public abstract class AbstractObservableValue<V> extends Observer<ChangeEvent> implements ObservableValue<V> {

	protected void modifyLastModified(long oldValue, long newValue) {
		publishEvent(new ChangeEvent(this, newValue));
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
