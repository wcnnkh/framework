package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Wrapper;

public interface ThrowingRunnable<E extends Throwable> {
	public static interface ThrowingRunnableWrapper<E extends Throwable, W extends ThrowingRunnable<E>>
			extends ThrowingRunnable<E>, Wrapper<W> {
		@Override
		default ThrowingRunnable<E> compose(@NonNull ThrowingRunnable<? extends E> before) {
			return getSource().compose(before);
		}

		@Override
		default ThrowingRunnable<E> andThen(@NonNull ThrowingRunnable<? extends E> after) {
			return getSource().andThen(after);
		}

		@Override
		default <R extends Throwable> ThrowingRunnable<R> throwing(
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().throwing(throwingMapper);
		}

		@Override
		default RuntimeThrowingRunnable<RuntimeException> runtime() {
			return getSource().runtime();
		}

		@Override
		default <R extends RuntimeException> RuntimeThrowingRunnable<R> runtime(
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().runtime(throwingMapper);
		}

		@Override
		default ThrowingRunnable<E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
			return getSource().onClose(endpoint);
		}

		@Override
		default void run() throws E {
			getSource().run();
		}
	}

	public static class IgnoreThrowingRunnable<E extends Throwable> implements ThrowingRunnable<E> {
		private static final IgnoreThrowingRunnable<?> INSTANCE = new IgnoreThrowingRunnable<>();

		@Override
		public void run() throws E {
		}
	}

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> ThrowingRunnable<E> ignore() {
		return (IgnoreThrowingRunnable<E>) IgnoreThrowingRunnable.INSTANCE;
	}

	@RequiredArgsConstructor
	@Getter
	public static class MappingThrowingRunnable<E extends Throwable, R extends Throwable>
			implements ThrowingRunnable<R> {
		@NonNull
		private final ThrowingRunnable<? extends E> compose;
		@NonNull
		private final ThrowingRunnable<? extends R> andThen;
		@NonNull
		private final Function<? super E, ? extends R> throwingMapper;
		@NonNull
		private final ThrowingRunnable<? extends R> endpoint;

		@SuppressWarnings("unchecked")
		@Override
		public void run() throws R {
			try {
				compose.run();
				andThen.run();
			} catch (Throwable e) {
				throw throwingMapper.apply((E) e);
			} finally {
				endpoint.run();
			}
		}
	}

	public static class DefaultRuntimeThrowingRunnable<E extends Throwable, R extends RuntimeException>
			extends MappingThrowingRunnable<E, R> implements RuntimeThrowingRunnable<R> {

		public DefaultRuntimeThrowingRunnable(@NonNull ThrowingRunnable<? extends E> compose,
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			super(compose, ThrowingRunnable.ignore(), throwingMapper, ThrowingRunnable.ignore());
		}

		@Override
		public String toString() {
			return getCompose().toString();
		}
	}

	default ThrowingRunnable<E> compose(@NonNull ThrowingRunnable<? extends E> before) {
		return new MappingThrowingRunnable<>(before, this, Function.identity(), ignore());
	}

	default ThrowingRunnable<E> andThen(@NonNull ThrowingRunnable<? extends E> after) {
		return new MappingThrowingRunnable<>(this, after, Function.identity(), ignore());
	}

	default <R extends Throwable> ThrowingRunnable<R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingThrowingRunnable<>(this, ignore(), throwingMapper, ignore());
	}

	default <R extends RuntimeException> RuntimeThrowingRunnable<R> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new DefaultRuntimeThrowingRunnable<>(this, throwingMapper);
	}

	default RuntimeThrowingRunnable<RuntimeException> runtime() {
		return runtime((e) -> e instanceof RuntimeException ? ((RuntimeException) e) : new RuntimeException(e));
	}

	default ThrowingRunnable<E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
		return new MappingThrowingRunnable<>(this, ignore(), Function.identity(), endpoint);
	}

	void run() throws E;
}
