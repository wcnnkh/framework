package io.basc.framework.util;

public interface RegistrationWrapper<W extends Registration> extends Registration, Wrapper<W> {
	@Override
	default boolean isCancellable() {
		return getSource().isCancellable();
	}

	@Override
	default boolean cancel() {
		return getSource().cancel();
	}

	@Override
	default boolean isCancelled() {
		return getSource().isCancelled();
	}
}