package run.soeasy.framework.core.function;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Wrapper;

public interface ThrowingFunction<S, T, E extends Throwable> {
	public static interface ThrowingFunctionWrapper<S, T, E extends Throwable, W extends ThrowingFunction<S, T, E>>
			extends ThrowingFunction<S, T, E>, Wrapper<W> {
		@Override
		default <R> ThrowingFunction<R, T, E> compose(
				@NonNull ThrowingFunction<? super R, ? extends S, ? extends E> before) {
			return getSource().compose(before);
		}

		@Override
		default <R> ThrowingFunction<S, R, E> andThen(
				@NonNull ThrowingFunction<? super T, ? extends R, ? extends E> after) {
			return getSource().andThen(after);
		}

		@Override
		default <R extends Throwable> ThrowingFunction<S, T, R> throwing(
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().throwing(throwingMapper);
		}

		@Override
		default <R extends RuntimeException> RuntimeThrowingFunction<S, T, R> runtime(
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().runtime(throwingMapper);
		}

		@Override
		default RuntimeThrowingFunction<S, T, RuntimeException> runtime() {
			return getSource().runtime();
		}

		@Override
		default T apply(S source) throws E {
			return getSource().apply(source);
		}
	}

	public static class IdentityThrowingFunction<T, E extends Throwable> implements ThrowingFunction<T, T, E> {
		private static final IdentityThrowingFunction<?, ?> INSTANCE = new IdentityThrowingFunction<>();

		@Override
		public T apply(T source) throws E {
			return source;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> ThrowingFunction<T, T, E> identity() {
		return (ThrowingFunction<T, T, E>) IdentityThrowingFunction.INSTANCE;
	}

	@RequiredArgsConstructor
	@Getter
	public static class MappingThrowingFunction<S, T, E extends Throwable, V, R extends Throwable>
			implements ThrowingFunction<S, V, R> {
		@NonNull
		private final ThrowingFunction<? super S, ? extends T, ? extends E> compose;
		@NonNull
		private final ThrowingFunction<? super T, ? extends V, ? extends R> andThen;
		@NonNull
		private final Function<? super E, ? extends R> throwingMapper;

		@SuppressWarnings("unchecked")
		@Override
		public V apply(S source) throws R {
			try {
				return andThen.apply(compose.apply(source));
			} catch (Throwable e) {
				throw throwingMapper.apply((E) e);
			}
		}
	}

	public static class DefaultRuntimeThrowingFunction<S, T, E extends Throwable, R extends RuntimeException>
			extends MappingThrowingFunction<S, T, E, T, R> implements RuntimeThrowingFunction<S, T, R> {

		public DefaultRuntimeThrowingFunction(@NonNull ThrowingFunction<? super S, ? extends T, ? extends E> compose,
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			super(compose, identity(), throwingMapper);
		}

		@Override
		public String toString() {
			return getCompose().toString();
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class PipelineReactor<S, T, E extends Throwable, W extends ThrowingFunction<S, T, E>>
			implements Reactor<S, T, E> {
		@NonNull
		private final W source;
		@NonNull
		private final ThrowingConsumer<? super T, ? extends E> endpoint;

		@Override
		public T apply(S source) throws E {
			T target = this.source.apply(source);
			try {
				return target;
			} finally {
				close(target);
			}
		}

		@Override
		public void close(T target) throws E {
			endpoint.accept(target);
		}

		@Override
		public Reactor<S, T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
			return new PipelineReactor<>(this.source, (target) -> {
				try {
					endpoint.accept(target);
				} finally {
					ThrowingFunction.PipelineReactor.this.close(target);
				}
			});
		}
	}

	@RequiredArgsConstructor
	public static class ThrowingFunctionPipeline<S, T, E extends Throwable, W extends ThrowingSupplier<? extends S, ? extends E>, P extends ThrowingFunction<? super S, ? extends T, ? extends E>>
			implements Pipeline<T, E> {
		@NonNull
		protected final W source;
		@NonNull
		protected final P pipeline;
		protected final ThrowingRunnable<? extends E> processor;
		private volatile Supplier<? extends S> supplier;
		private final AtomicBoolean closed = new AtomicBoolean(false);

		@Override
		public void close() throws E {
			synchronized (this) {
				if (closed.compareAndSet(false, true)) {
					if (processor != null) {
						processor.run();
					}
				}
			}
		}

		@Override
		public T get() throws E {
			if (supplier == null) {
				synchronized (this) {
					if (supplier == null) {
						S target = source.get();
						supplier = () -> target;
					}
				}
			}

			S target = supplier.get();
			return pipeline.apply(target);
		}

		@Override
		public boolean isClosed() {
			return closed.get();
		}
	}

	default <R> ThrowingFunction<R, T, E> compose(
			@NonNull ThrowingFunction<? super R, ? extends S, ? extends E> before) {
		return new MappingThrowingFunction<>(before, this, Function.identity());
	}

	default <R> ThrowingFunction<S, R, E> andThen(
			@NonNull ThrowingFunction<? super T, ? extends R, ? extends E> after) {
		return new MappingThrowingFunction<>(this, after, Function.identity());
	}

	default <R extends Throwable> ThrowingFunction<S, T, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingThrowingFunction<>(this, identity(), throwingMapper);
	}

	default <R extends RuntimeException> RuntimeThrowingFunction<S, T, R> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new DefaultRuntimeThrowingFunction<>(this, throwingMapper);
	}

	default RuntimeThrowingFunction<S, T, RuntimeException> runtime() {
		return runtime((e) -> e instanceof RuntimeException ? ((RuntimeException) e) : new RuntimeException(e));
	}

	default Pipeline<T, E> newPipeline(@NonNull ThrowingSupplier<? extends S, ? extends E> supplier) {
		return new ThrowingFunctionPipeline<>(supplier, this, null);
	}

	default Reactor<S, T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
		return new PipelineReactor<>(this, endpoint);
	}

	T apply(S source) throws E;
}
