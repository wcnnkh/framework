package run.soeasy.framework.core.function;

import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.function.ThrowingFunction.ThrowingFunctionToSource;
import run.soeasy.framework.core.function.lang.ThrowingConsumer;
import run.soeasy.framework.core.function.lang.ThrowingRunnable;
import run.soeasy.framework.core.function.runtime.RuntimeThrowingSupplier;
import run.soeasy.framework.core.function.stream.Pool;
import run.soeasy.framework.core.function.stream.Source;

public interface ThrowingSupplier<T, E extends Throwable> {

	public static interface ThrowingSupplierWrapper<T, E extends Throwable, W extends ThrowingSupplier<T, E>>
			extends ThrowingSupplier<T, E>, Wrapper<W> {
		@Override
		default Source<T, E> closeable() {
			return getSource().closeable();
		}

		@Override
		default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
			return getSource().onClose(consumer);
		}

		@Override
		default Source<T, E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
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

		@Override
		default <R extends Exception> Callable<T> asCallable(@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().asCallable(throwingMapper);
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

		@Override
		public V get() throws T {
			return run(this.source);
		}

		@SuppressWarnings("unchecked")
		public V run(ThrowingSupplier<? extends S, ? extends E> supplier) throws T {
			try {
				S source = supplier.get();
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

	public static class CloseableSupplier<T, E extends Throwable, W extends ThrowingSupplier<? extends T, ? extends E>>
			extends ThrowingFunctionToSource<T, T, E, W, ThrowingFunction<? super T, ? extends T, ? extends E>> {

		public CloseableSupplier(@NonNull W source, ThrowingRunnable<? extends E> processor) {
			super(source, ThrowingFunction.identity(), processor);
		}

	}

	default Source<T, E> closeable() {
		return new CloseableSupplier<>(this, null);
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

	default Source<T, E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
		return new CloseableSupplier<>(this, endpoint);
	}

	default <R> ThrowingSupplier<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return new MappingThrowingSupplier<>(this, mapper, ThrowingConsumer.ignore(), Function.identity());
	}

	default <R extends Throwable> ThrowingSupplier<T, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				throwingMapper);
	}

	public static class RuntimeSupplier<V, E extends Throwable, T extends RuntimeException, W extends ThrowingSupplier<? extends V, ? extends E>>
			extends MappingThrowingSupplier<V, V, E, T, W> implements RuntimeThrowingSupplier<V, T> {

		public RuntimeSupplier(@NonNull W source, @NonNull Function<? super E, ? extends T> throwingMapper) {
			super(source, ThrowingFunction.identity(), ThrowingConsumer.ignore(), throwingMapper);
		}

		@Override
		public String toString() {
			return source.toString();
		}
	}

	default <R extends RuntimeException> RuntimeThrowingSupplier<T, R> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new RuntimeSupplier<>(this, throwingMapper);
	}

	default RuntimeThrowingSupplier<T, RuntimeException> runtime() {
		return runtime(
				(e) -> e instanceof RuntimeException ? ((RuntimeException) e) : new UndeclaredThrowableException(e));
	}

	public static class SupplierAsCallable<V, E extends Throwable, T extends Exception, W extends ThrowingSupplier<? extends V, ? extends E>>
			extends MappingThrowingSupplier<V, V, E, T, W> implements Callable<V> {

		public SupplierAsCallable(@NonNull W source, @NonNull Function<? super E, ? extends T> throwingMapper) {
			super(source, ThrowingFunction.identity(), ThrowingConsumer.ignore(), throwingMapper);
		}

		@Override
		public final V call() throws T {
			return get();
		}
	}

	default <R extends Exception> Callable<T> asCallable(@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new SupplierAsCallable<>(this, throwingMapper);
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
