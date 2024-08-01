package io.basc.framework.util.register;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;

public interface ServiceRegistry<S, R extends ServiceRegistration<S>> extends Registry<S, R>, ServiceLoader<S> {
	default void deregister(S service) {
		for (ServiceRegistration<S> registration : getRegistrations()) {
			if (ObjectUtils.equals(registration.getPayload(), service)) {
				registration.deregister();
			}
		}
	}

	@Override
	default Elements<S> getServices() {
		return getRegistrations().filter((e) -> !e.isInvalid()).map((e) -> e.getPayload());
	}

	/**
	 * 期望支持批量注册
	 */
	@Override
	default R register(S item) throws RegistrationException {
		Registrations<R> registrations = registers(Elements.singleton(item));
		return registrations.getRegistrations().first();
	}

	@Override
	ServiceBatchRegistration<S, R> registers(Iterable<? extends S> items) throws RegistrationException;

	/**
	 * 重新加载服务注册表
	 */
	@Override
	void reload();
}
