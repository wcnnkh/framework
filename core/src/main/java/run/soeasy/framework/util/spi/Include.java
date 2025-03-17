package run.soeasy.framework.util.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.collections.ServiceLoader;
import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.exchange.Receipt.Receipted;

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
		public ServiceLoader<S> getSource() {
			return source;
		}

		@Override
		public <U> Configured<U> convert(@NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
			return Configured.super.convert(converter);
		}
	}

	@FunctionalInterface
	public static interface IncludeWrapper<S, W extends Include<S>>
			extends Include<S>, RegistrationWrapper<W>, ServiceLoaderWrapper<S, W> {

		@Override
		default <U> Include<U> convert(@NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
			return getSource().convert(converter);
		}

		@Override
		default Include<S> and(Registration registration) {
			return getSource().and(registration);
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

	public static class ConvertedInclude<S, T, W extends Include<S>> extends ConvertedServiceLoader<S, T, W>
			implements Include<T> {

		public ConvertedInclude(@NonNull W target,
				@NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
			super(target, converter);
		}

		@Override
		public boolean cancel() {
			return getTarget().cancel();
		}

		@Override
		public boolean isCancellable() {
			return getTarget().isCancellable();
		}

		@Override
		public boolean isCancelled() {
			return getTarget().isCancelled();
		}

		@Override
		public <U> Include<U> convert(Function<? super Stream<T>, ? extends Stream<U>> converter) {
			return Include.super.convert(converter);
		}

	}

	@Override
	default <U> Include<U> convert(@NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return new ConvertedInclude<>(this, converter);
	}

	@Override
	default Include<S> and(Registration registration) {
		return new And<>(this, registration);
	}
}
