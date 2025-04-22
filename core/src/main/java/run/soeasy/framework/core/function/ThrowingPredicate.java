package run.soeasy.framework.core.function;

import java.util.function.Function;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface ThrowingPredicate<S, E extends Throwable> {
	@RequiredArgsConstructor
	public static class AlwaysBooleanPredicat<S, E extends Throwable> implements ThrowingPredicate<S, E> {
		private static final AlwaysBooleanPredicat<?, ?> TRUE = new AlwaysBooleanPredicat<>(true);
		private static final AlwaysBooleanPredicat<?, ?> FALSE = new AlwaysBooleanPredicat<>(false);

		private final boolean value;

		@Override
		public boolean test(S source) throws E {
			return value;
		}
	}

	@SuppressWarnings("unchecked")
	public static <S, E extends Throwable> ThrowingPredicate<S, E> alwaysFalse() {
		return (ThrowingPredicate<S, E>) AlwaysBooleanPredicat.FALSE;
	}

	@SuppressWarnings("unchecked")
	public static <S, E extends Throwable> ThrowingPredicate<S, E> alwaysTrue() {
		return (ThrowingPredicate<S, E>) AlwaysBooleanPredicat.TRUE;
	}

	@RequiredArgsConstructor
	@Getter
	public static class MergedThrowingPredicate<S, E extends Throwable, T, R extends Throwable>
			implements ThrowingPredicate<T, R> {
		@NonNull
		private final ThrowingFunction<? super T, ? extends S, ? extends R> mapper;
		@NonNull
		private final ThrowingPredicate<? super S, ? extends E> predicate;
		@NonNull
		private final Function<? super E, ? extends R> throwingMapper;
		@NonNull
		private final ThrowingConsumer<? super S, ? extends R> endpoint;

		@SuppressWarnings("unchecked")
		@Override
		public boolean test(T target) throws R {
			S source = mapper.apply(target);
			try {
				return predicate.test(source);
			} catch (Throwable e) {
				throw throwingMapper.apply((E) e);
			} finally {
				endpoint.accept(source);
			}
		}
	}

	public static class RuntimeThrowingPredicate<S, E extends Throwable, R extends RuntimeException>
			extends MergedThrowingPredicate<S, E, S, R> implements Predicate<S> {

		public RuntimeThrowingPredicate(@NonNull ThrowingPredicate<? super S, ? extends E> compose,
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			super(ThrowingFunction.identity(), compose, throwingMapper, ThrowingConsumer.ignore());
		}

		@Override
		public RuntimeThrowingPredicate<S, R, R> negate() {
			return new RuntimeThrowingPredicate<>(this.negate(), Function.identity());
		}

		@Override
		public String toString() {
			return getPredicate().toString();
		}
	}

	default <R> ThrowingPredicate<R, E> map(@NonNull ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
		return new MergedThrowingPredicate<>(mapper, this, Function.identity(), ThrowingConsumer.ignore());
	}

	default ThrowingPredicate<S, E> and(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
		return (t) -> test(t) && other.test(t);
	}

	default ThrowingPredicate<S, E> negate() {
		return (t) -> !test(t);
	}

	default ThrowingPredicate<S, E> or(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
		return (t) -> test(t) || other.test(t);
	}

	default <R extends Throwable> ThrowingPredicate<S, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MergedThrowingPredicate<>(ThrowingFunction.identity(), this, throwingMapper,
				ThrowingConsumer.ignore());
	}

	default <R extends RuntimeException> RuntimeThrowingPredicate<S, E, R> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new RuntimeThrowingPredicate<>(this, throwingMapper);
	}

	default Predicate<S> runtime() {
		return runtime((e) -> e instanceof RuntimeException ? ((RuntimeException) e) : new RuntimeException(e));
	}

	boolean test(S source) throws E;
}
