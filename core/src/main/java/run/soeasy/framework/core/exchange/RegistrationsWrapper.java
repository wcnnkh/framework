package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.ListableWrapper;

@FunctionalInterface
public interface RegistrationsWrapper<R extends Registration, W extends Registrations<R>>
		extends Registrations<R>, RegistrationWrapper<W>, ListableWrapper<R, W> {
	@Override
	default boolean cancel() {
		return getSource().cancel();
	}

	@Override
	default boolean isCancellable() {
		return getSource().isCancellable();
	}

	@Override
	default boolean isCancelled() {
		return getSource().isCancelled();
	}
}