package io.basc.framework.util.register;

import java.util.function.Function;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.ConsumeProcessor;
import lombok.NonNull;

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

	/**
	 * 映射每一个registration
	 * 
	 * @param <R>
	 * @param mapper
	 * @return
	 */
	default <R extends Registration> Registrations<R> map(@NonNull Function<? super T, ? extends R> mapper) {
		return () -> getRegistrations().map(mapper);
	}
}
