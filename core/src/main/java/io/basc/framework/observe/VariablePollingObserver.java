package io.basc.framework.observe;

import java.io.IOException;

import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.event.batch.BatchEventDispatcher;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.util.watch.Variable;
import lombok.NonNull;

public class VariablePollingObserver<T extends Variable> extends VariablePolling<ChangeEvent<Long>> {
	public VariablePollingObserver(@NonNull BatchEventDispatcher<ChangeEvent<Long>> eventDispatcher,
			@NonNull T variable) {
		super(eventDispatcher);
		this.variable = variable;
		Long lastModified = lastModified(variable);
		if (lastModified != null) {
			getAtomicLastModified().set(lastModified);
		}
	}

	private static Logger logger = LoggerFactory.getLogger(VariablePollingObserver.class);
	private final T variable;

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
		getEventDispatcher().publishEvent(new UpdateEvent<>(newValue, oldValue));
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
