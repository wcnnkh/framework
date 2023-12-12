package io.basc.framework.observe;

import java.io.IOException;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;

public class VariablePollingObserver<T extends Variable> extends VariablePolling<ObservableEvent<Long>> {
	private static Logger logger = LoggerFactory.getLogger(VariablePollingObserver.class);
	private final T variable;

	public VariablePollingObserver(T variable) {
		Assert.requiredArgument(variable != null, "variable");
		this.variable = variable;
		Long lastModified = lastModified(variable);
		if (lastModified != null) {
			getAtomicLastModified().set(lastModified);
		}
	}

	protected Long lastModified(T variable) {
		try {
			return variable.lastModified();
		} catch (IOException e) {
			logger.trace(e, "Unable to obtain[{}] lastModified", variable);
			return null;
		}
	}

	public T getVariable() {
		return variable;
	}

	protected void modifyLastModified(long oldValue, long newValue) {
		publishEvent(new ObservableEvent<>(this, new Changed<>(oldValue, newValue)));
	}

	@Override
	public void run() {
		setLastModified(lastModified(), lastModified(variable));
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
