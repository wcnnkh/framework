package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;

public interface Configured<S> extends Include<S>, Receipt {

	@FunctionalInterface
	public static interface ConfiguredWrapper<S, W extends Configured<S>>
			extends Configured<S>, IncludeWrapper<S, W>, ReceiptWrapper<W> {
		@Override
		default <U> Configured<U> convert(boolean resize,
				@NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
			return getSource().convert(resize, converter);
		}

		@Override
		default Configured<S> and(Registration registration) {
			return getSource().and(registration);
		}
	}

	public static class And<S, W extends Configured<S>> extends Include.And<S, W> implements ConfiguredWrapper<S, W> {

		public And(@NonNull W source, @NonNull Registration registration) {
			super(source, registration);
		}

		@Override
		public Configured<S> and(Registration registration) {
			return ConfiguredWrapper.super.and(registration);
		}
	}

	public static class ConvertedConfigured<S, T, W extends Configured<S>> extends ConvertedInclude<S, T, W>
			implements Configured<T> {

		public ConvertedConfigured(@NonNull W target, boolean resize,
				@NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
			super(target, resize, converter);
		}

		@Override
		public <U> Configured<U> convert(boolean resize, Function<? super Stream<T>, ? extends Stream<U>> converter) {
			return Configured.super.convert(resize, converter);
		}

		@Override
		public Throwable cause() {
			return getTarget().cause();
		}

		@Override
		public boolean isDone() {
			return getTarget().isDone();
		}

		@Override
		public boolean isSuccess() {
			return getTarget().isSuccess();
		}

	}

	@Override
	default Configured<S> and(Registration registration) {
		return new And<>(this, registration);
	}

	public static final Configured<?> FAILURE_CONFIGURED = new Included<>(true, false, null);

	@SuppressWarnings("unchecked")
	public static <S> Configured<S> failure() {
		return (Configured<S>) FAILURE_CONFIGURED;
	}

	@Override
	default <U> Configured<U> convert(boolean resize,
			@NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return new ConvertedConfigured<>(this, resize, converter);
	}
}
