package io.basc.framework.util.spi;

import io.basc.framework.util.Registration;
import io.basc.framework.util.ServiceLoader;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Include<S> extends Registration, ServiceLoader<S> {
	@Getter
	@RequiredArgsConstructor
	public static class And<S, W extends Include<S>> implements IncludeWrapper<S, W> {
		@NonNull
		private final W source;
		@NonNull
		private final Registration registration;

		@Override
		public Include<S> and(Registration registration) {
			return new And<>(source, this.registration.and(registration));
		}

		@Override
		public boolean cancel() {
			return IncludeWrapper.super.cancel() && registration.cancel();
		}

		@Override
		public boolean isCancellable() {
			return IncludeWrapper.super.isCancellable() || registration.isCancellable();
		}

		@Override
		public boolean isCancelled() {
			return IncludeWrapper.super.isCancelled() && registration.isCancelled();
		}
	}

	@Override
	default Include<S> and(Registration registration) {
		return new And<>(this, registration);
	}
}
