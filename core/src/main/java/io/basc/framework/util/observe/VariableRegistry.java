package io.basc.framework.util.observe;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.Registry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VariableRegistry<T extends Variable> extends Poller implements Registry<T, Registration> {
	@NonNull
	private final Registry<T, ? extends Registration> registry;
	@NonNull
	private final EventPublishService<ChangeEvent<T>> eventPublishService;

	@Override
	public Elements<T> getServices() {
		return registry.getServices();
	}

	@Override
	public void reload() {
		registry.reload();
	}

	@Override
	public void run() {
		for (T variable : registry.getServices()) {
			VariablePoller<T> variablePoller = new VariablePoller<>(variable, eventPublishService);
			variablePoller.run();
		}
	}

	@Override
	public Registration register(T element) throws RegistrationException {
		return registry.register(element);
	}

}
