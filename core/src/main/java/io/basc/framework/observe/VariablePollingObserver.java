package io.basc.framework.observe;

import io.basc.framework.util.Assert;

public class VariablePollingObserver<T extends Variable> extends VariablePolling<ObservableEvent<Long>> {
	private final T variable;

	public VariablePollingObserver(T variable) {
		Assert.requiredArgument(variable != null, "variable");
		this.variable = variable;
		getAtomicLastModified().set(variable.lastModified());
	}

	public T getVariable() {
		return variable;
	}

	protected void modifyLastModified(long oldValue, long newValue) {
		publishEvent(new ObservableEvent<>(this, new Changed<>(oldValue, newValue)));
	}

	@Override
	public void run() {
		setLastModified(lastModified(), variable.lastModified());
	}

	@Override
	public long setLastModified(long lastModified) {
		long oldValue = super.setLastModified(lastModified);
		modifyLastModified(oldValue, lastModified);
		return oldValue;
	}

	@Override
	public boolean setLastModified(long oldValue, long newValue) {
		if (super.setLastModified(oldValue, newValue)) {
			modifyLastModified(oldValue, newValue);
			return true;
		}
		return false;
	}
}
