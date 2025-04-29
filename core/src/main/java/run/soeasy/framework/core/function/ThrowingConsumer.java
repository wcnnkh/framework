package run.soeasy.framework.core.function;

import java.util.Iterator;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Wrapper;

public interface ThrowingConsumer<S, E extends Throwable> {
	public static interface ThrowingConsumerWrapper<S, E extends Throwable, W extends ThrowingConsumer<S, E>>
			extends ThrowingConsumer<S, E>, Wrapper<W> {
		@Override
		default <R> ThrowingConsumer<R, E> map(ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
			return getSource().map(mapper);
		}

		@Override
		default ThrowingConsumer<S, E> compose(@NonNull ThrowingConsumer<? super S, ? extends E> before) {
			return getSource().compose(before);
		}

		@Override
		default ThrowingConsumer<S, E> andThen(@NonNull ThrowingConsumer<? super S, ? extends E> after) {
			return getSource().andThen(after);
		}

		@Override
		default <R extends Throwable> ThrowingConsumer<S, R> throwing(
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().throwing(throwingMapper);
		}

		@Override
		default RuntimeThrowingConsumer<S, RuntimeException> runtime() {
			return getSource().runtime();
		}

		@Override
		default <R extends RuntimeException> RuntimeThrowingConsumer<S, R> runtime(
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().runtime(throwingMapper);
		}

		@Override
		default ThrowingConsumer<S, E> onClose(@NonNull ThrowingConsumer<? super S, ? extends E> endpoint) {
			return getSource().onClose(endpoint);
		}

		@Override
		default void accept(S source) throws E {
			getSource().accept(source);
		}
	}

	public static class IgnoreThrowingConsumer<S, E extends Throwable> implements ThrowingConsumer<S, E> {
		private static final IgnoreThrowingConsumer<?, ?> INSTANCE = new IgnoreThrowingConsumer<>();

		@Override
		public void accept(S source) throws E {
		}
	}

	@SuppressWarnings("unchecked")
	public static <S, E extends Throwable> ThrowingConsumer<S, E> ignore() {
		return (ThrowingConsumer<S, E>) IgnoreThrowingConsumer.INSTANCE;
	}

	public static <S, E extends Throwable> void acceptAll(@NonNull Iterator<? extends S> sourceIterator,
			@NonNull ThrowingConsumer<? super S, ? extends E> consumer) throws E {
		if (sourceIterator.hasNext()) {
			try {
				consumer.accept(sourceIterator.next());
			} finally {
				acceptAll(sourceIterator, consumer);
			}
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class MappingThrowingConsumer<S, E extends Throwable, T, R extends Throwable>
			implements ThrowingConsumer<T, R> {
		@NonNull
		private final ThrowingFunction<? super T, ? extends S, ? extends R> mapper;
		@NonNull
		private final ThrowingConsumer<? super S, ? extends E> compose;
		@NonNull
		private final ThrowingConsumer<? super S, ? extends R> andThen;
		@NonNull
		private final Function<? super E, ? extends R> throwingMapper;
		@NonNull
		private final ThrowingConsumer<? super S, ? extends R> endpoint;

		@SuppressWarnings("unchecked")
		@Override
		public void accept(T target) throws R {
			S source = mapper.apply(target);
			try {
				compose.accept(source);
				andThen.accept(source);
			} catch (Throwable e) {
				throw throwingMapper.apply((E) e);
			} finally {
				endpoint.accept(source);
			}
		}
	}

	public static class RuntimeConsumer<S, E extends Throwable, R extends RuntimeException>
			extends MappingThrowingConsumer<S, E, S, R> implements RuntimeThrowingConsumer<S, R> {

		public RuntimeConsumer(@NonNull ThrowingConsumer<? super S, ? extends E> compose,
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			super(ThrowingFunction.identity(), compose, ignore(), throwingMapper, ignore());
		}

		@Override
		public String toString() {
			return getCompose().toString();
		}
	}

	default <R> ThrowingConsumer<R, E> map(ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
		return new MappingThrowingConsumer<>(mapper, this, ignore(), Function.identity(), ignore());
	}

	default ThrowingConsumer<S, E> compose(@NonNull ThrowingConsumer<? super S, ? extends E> before) {
		return new MappingThrowingConsumer<>(ThrowingFunction.identity(), before, this, Function.identity(), ignore());
	}

	default ThrowingConsumer<S, E> andThen(@NonNull ThrowingConsumer<? super S, ? extends E> after) {
		return new MappingThrowingConsumer<>(ThrowingFunction.identity(), this, after, Function.identity(), ignore());
	}

	default <R extends Throwable> ThrowingConsumer<S, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingThrowingConsumer<>(ThrowingFunction.identity(), this, ignore(), throwingMapper, ignore());
	}

	default <R extends RuntimeException> RuntimeThrowingConsumer<S, R> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new RuntimeConsumer<>(this, throwingMapper);
	}

	default RuntimeThrowingConsumer<S, RuntimeException> runtime() {
		return runtime((e) -> e instanceof RuntimeException ? ((RuntimeException) e) : new RuntimeException(e));
	}

	default ThrowingConsumer<S, E> onClose(@NonNull ThrowingConsumer<? super S, ? extends E> endpoint) {
		return new MappingThrowingConsumer<>(ThrowingFunction.identity(), this, ignore(), Function.identity(),
				endpoint);
	}

	void accept(S source) throws E;
}
