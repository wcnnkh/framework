package io.basc.framework.util.spi;

import io.basc.framework.util.Receipt.Receipted;
import io.basc.framework.util.Registration;
import io.basc.framework.util.ServiceLoader;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Include<S> extends Registration, ServiceLoader<S> {

	public static class Included<S> extends Receipted
			implements Configured<S>, ServiceLoaderWrapper<S, ServiceLoader<S>> {
		private static final long serialVersionUID = 1L;
		private final ServiceLoader<S> source;

		public Included(boolean done, boolean success, Throwable cause) {
			this(done, success, cause, ServiceLoader.empty());
		}

		public Included(boolean done, boolean success, Throwable cause, ServiceLoader<S> source) {
			super(done, success, cause);
			this.source = source;
		}

		@Override
		public void reload() {
			source.reload();
		}

		@Override
		public ServiceLoader<S> getSource() {
			return source;
		}
	}

	public static interface IncludeWrapper<S, W extends Include<S>>
			extends Include<S>, RegistrationWrapper<W>, ServiceLoaderWrapper<S, W> {
		@Override
		default void reload() {
			getSource().reload();
		}
	}

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
