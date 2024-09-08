package io.basc.framework.util.observe;

import io.basc.framework.util.Wrapper;

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
