package io.basc.framework.observe.register;

import io.basc.framework.observe.Variable;
import io.basc.framework.observe.VariablePollingObserver;
import io.basc.framework.register.Registration;

public class VariableRegistry<T extends Variable> extends PollingRegistry<VariablePollingObserver<T>> {

	public Registration register(T variable) {
		VariablePollingObserver<T> observer = new VariablePollingObserver<>(variable);
		return register(observer);
	}

	@Override
	public boolean start() {
		return startTimerTask();
	}

	@Override
	public boolean stop() {
		return stopTimerTask();
	}

}
