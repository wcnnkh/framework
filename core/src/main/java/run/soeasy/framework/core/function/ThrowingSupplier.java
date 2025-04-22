package run.soeasy.framework.core.function;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.ThrowingFunction.ThrowingFunctionPipeline;

public interface ThrowingSupplier<T, E extends Throwable> {
	@RequiredArgsConstructor
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

	public static class RuntimeThrowingSupplier<V, E extends Throwable, T extends RuntimeException, W extends ThrowingSupplier<? extends V, ? extends E>>
			extends MappingThrowingSupplier<V, V, E, T, W> implements Supplier<V> {

		public RuntimeThrowingSupplier(@NonNull W source, @NonNull Function<? super E, ? extends T> throwingMapper) {
			super(source, ThrowingFunction.identity(), ThrowingConsumer.ignore(), throwingMapper);
		}

		@Override
		public String toString() {
			return source.toString();
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

	default <R extends RuntimeException> RuntimeThrowingSupplier<T, E, R, ? extends ThrowingSupplier<T, E>> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new RuntimeThrowingSupplier<>(this, throwingMapper);
	}

	default Supplier<T> runtime() {
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

	T get() throws E;
}
