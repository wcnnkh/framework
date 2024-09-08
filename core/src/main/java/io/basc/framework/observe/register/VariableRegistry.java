package io.basc.framework.observe.register;

import io.basc.framework.observe.VariablePollingObserver;
import io.basc.framework.util.observe.poll.Variable;
import io.basc.framework.util.register.Registration;

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
