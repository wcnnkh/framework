package run.soeasy.framework.core.function;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.function.ThrowingFunction.ThrowingFunctionPipeline;

public interface ThrowingSupplier<T, E extends Throwable> {
	public static interface ThrowingSupplierWrapper<T, E extends Throwable, W extends ThrowingSupplier<T, E>>
			extends ThrowingSupplier<T, E>, Wrapper<W> {
		@Override
		default Pipeline<T, E> newPipeline() {
			return getSource().newPipeline();
		}

		@Override
		default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
			return getSource().onClose(consumer);
		}

		@Override
		default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
			return getSource().onClose(endpoint);
		}

		@Override
		default <R> ThrowingSupplier<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
			return getSource().map(mapper);
		}

		@Override
		default <R extends Throwable> ThrowingSupplier<T, R> throwing(
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().throwing(throwingMapper);
		}

		@Override
		default <R extends RuntimeException> RuntimeThrowingSupplier<T, R> runtime(
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().runtime(throwingMapper);
		}

		@Override
		default RuntimeThrowingSupplier<T, RuntimeException> runtime() {
			return getSource().runtime();
		}

		@Override
		default T get() throws E {
			return getSource().get();
		}

		@Override
		default ThrowingOptional<T, E> optional() {
			return getSource().optional();
		}

		@Override
		default Optional<T> offline() throws E {
			return getSource().offline();
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class ValueThrowingSupplier<T, E extends Throwable> implements ThrowingSupplier<T, E>, Serializable {
		private static final long serialVersionUID = 1L;
		protected final T value;

		@Override
		public T get() throws E {
			return value;
		}
	}

	public static <T, E extends Throwable> ThrowingSupplier<T, E> forValue(T value) {
		return new ValueThrowingSupplier<>(value);
	}

	@RequiredArgsConstructor
	public static class MappingThrowingSupplier<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<? extends S, ? extends E>>
			implements ThrowingSupplier<V, T> {
		@NonNull
		protected final W source;
		@NonNull
		protected final ThrowingFunction<? super S, ? extends V, T> mapper;
		protected final ThrowingConsumer<? super S, ? extends E> endpoint;
		@NonNull
		protected final Function<? super E, ? extends T> throwingMapper;

		@SuppressWarnings("unchecked")
		@Override
		public V get() throws T {
			try {
				S source = this.source.get();
				try {
					return mapper.apply(source);
				} finally {
					endpoint.accept(source);
				}
			} catch (Throwable e) {
				throw throwingMapper.apply((E) e);
			}
		}

		@Override
		public <R> ThrowingSupplier<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
			return new MappingThrowingSupplier<>(this.source, this.mapper.andThen(mapper), endpoint, throwingMapper);
		}
	}

	public static class ThrowingSupplierPipeline<T, E extends Throwable, W extends ThrowingSupplier<? extends T, ? extends E>>
			extends ThrowingFunctionPipeline<T, T, E, W, ThrowingFunction<? super T, ? extends T, ? extends E>> {

		public ThrowingSupplierPipeline(@NonNull W source, ThrowingRunnable<? extends E> processor) {
			super(source, ThrowingFunction.identity(), processor);
		}

	}

	default Pipeline<T, E> newPipeline() {
		return new ThrowingSupplierPipeline<>(this, null);
	}

	@RequiredArgsConstructor
	public static class ThrowingSupplierPool<T, E extends Throwable, W extends ThrowingSupplier<T, E>>
			implements Pool<T, E> {
		@NonNull
		protected final W source;
		@NonNull
		protected final ThrowingConsumer<? super T, ? extends E> endpoint;

		@Override
		public void close(T target) throws E {
			endpoint.accept(target);
		}

		@Override
		public T get() throws E {
			return source.get();
		}
	}

	default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
		return new ThrowingSupplierPool<>(this, consumer);
	}

	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
		return new ThrowingSupplierPipeline<>(this, endpoint);
	}

	default <R> ThrowingSupplier<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return new MappingThrowingSupplier<>(this, mapper, ThrowingConsumer.ignore(), Function.identity());
	}

	default <R extends Throwable> ThrowingSupplier<T, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				throwingMapper);
	}

	public static class DefaultRuntimeThrowingSupplier<V, E extends Throwable, T extends RuntimeException, W extends ThrowingSupplier<? extends V, ? extends E>>
			extends MappingThrowingSupplier<V, V, E, T, W> implements RuntimeThrowingSupplier<V, T> {

		public DefaultRuntimeThrowingSupplier(@NonNull W source,
				@NonNull Function<? super E, ? extends T> throwingMapper) {
			super(source, ThrowingFunction.identity(), ThrowingConsumer.ignore(), throwingMapper);
		}

		@Override
		public String toString() {
			return source.toString();
		}
	}

	default <R extends RuntimeException> RuntimeThrowingSupplier<T, R> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new DefaultRuntimeThrowingSupplier<>(this, throwingMapper);
	}

	default RuntimeThrowingSupplier<T, RuntimeException> runtime() {
		return runtime((e) -> e instanceof RuntimeException ? ((RuntimeException) e) : new RuntimeException(e));
	}

	@RequiredArgsConstructor
	public static class ThrowingSupplierOptional<T, E extends Throwable, W extends ThrowingSupplier<? extends T, ? extends E>>
			implements ThrowingOptional<T, E> {
		@NonNull
		protected final W source;

		@Override
		public <R, X extends Throwable> R apply(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			return mapper.apply(source.get());
		}
	}

	default ThrowingOptional<T, E> optional() {
		return new ThrowingSupplierOptional<>(this);
	}

	default Optional<T> offline() throws E {
		return Optional.ofNullable(get());
	}

	T get() throws E;
}
