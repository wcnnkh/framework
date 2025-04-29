package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.function.RuntimeThrowingPredicate.RuntimePredicate;

public interface ThrowingPredicate<S, E extends Throwable> {
	public static interface ThrowingPredicateWrapper<S, E extends Throwable, W extends ThrowingPredicate<S, E>>
			extends ThrowingPredicate<S, E>, Wrapper<W> {
		@Override
		default <R> ThrowingPredicate<R, E> map(@NonNull ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
			return getSource().map(mapper);
		}

		@Override
		default ThrowingPredicate<S, E> and(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
			return getSource().and(other);
		}

		@Override
		default ThrowingPredicate<S, E> negate() {
			return getSource().negate();
		}

		@Override
		default ThrowingPredicate<S, E> or(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
			return getSource().or(other);
		}

		@Override
		default <R extends Throwable> ThrowingPredicate<S, R> throwing(
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().throwing(throwingMapper);
		}

		@Override
		default RuntimeThrowingPredicate<S, RuntimeException> runtime() {
			return getSource().runtime();
		}

		@Override
		default <R extends RuntimeException> RuntimeThrowingPredicate<S, R> runtime(
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().runtime(throwingMapper);
		}

		@Override
		default boolean test(S source) throws E {
			return getSource().test(source);
		}
	}

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

	default <R extends RuntimeException> RuntimeThrowingPredicate<S, R> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new RuntimePredicate<>(this, throwingMapper);
	}

	default RuntimeThrowingPredicate<S, RuntimeException> runtime() {
		return runtime((e) -> e instanceof RuntimeException ? ((RuntimeException) e) : new RuntimeException(e));
	}

	boolean test(S source) throws E;
}
