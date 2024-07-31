package io.basc.framework.util.register;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.ConsumeProcessor;

@FunctionalInterface
public interface Registrations<T extends Registration> extends Registration {
	/**
	 * 获取所有的注册
	 * 
	 * @return
	 */
	Elements<T> getRegistrations();

	@Override
	default boolean isInvalid() {
		return getRegistrations().allMatch(Registration::isInvalid);
	}

	@Override
	default void deregister() throws RegistrationException {
		ConsumeProcessor.consumeAll(getRegistrations().reverse(), Registration::deregister);
	}
}
