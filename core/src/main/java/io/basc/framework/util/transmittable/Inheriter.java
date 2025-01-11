package io.basc.framework.util.transmittable;

import io.basc.framework.util.function.Callable;
import io.basc.framework.util.function.Consumer;
import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Runnable;
import lombok.NonNull;

/**
 * 
 * B b = replay(capture()); try{ codeing... } finally{ restore(b); }
 * 
 * @author wcnnkh
 *
 * @param <A>
 * @param <B>
 */
public interface Inheriter<A, B> extends NativeInheriter<A, B> {
	public static class WrappedCallable<A, B, I extends Inheriter<A, B>, T, E extends Throwable, W extends Callable<? extends T, ? extends E>>
			extends Wrapper<A, B, I, W> implements Callable<T, E> {

		public WrappedCallable(W source, I inheriter) {
			super(source, inheriter);
		}

		public T call() throws E {
			B backup = inheriter.replay(capture);
			try {
				return this.source.call();
			} finally {
				inheriter.restore(backup);
			}
		};
	}

	public static class WrappedConsumer<A, B, I extends Inheriter<A, B>, S, E extends Throwable, W extends Consumer<? super S, ? extends E>>
			extends Wrapper<A, B, I, W> implements Consumer<S, E> {

		public WrappedConsumer(W source, I inheriter) {
			super(source, inheriter);
		}

		public void accept(S source) throws E {
			B backup = inheriter.replay(capture);
			try {
				this.source.accept(source);
			} finally {
				inheriter.restore(backup);
			}
		}
	}

	public static class WrappedFunction<A, B, I extends Inheriter<A, B>, S, T, E extends Throwable, W extends Function<? super S, ? extends T, ? extends E>>
			extends Wrapper<A, B, I, W> implements Function<S, T, E> {

		public WrappedFunction(W source, I inheriter) {
			super(source, inheriter);
		}

		@Override
		public T apply(S source) throws E {
			B backup = inheriter.replay(capture);
			try {
				return this.source.apply(source);
			} finally {
				inheriter.restore(backup);
			}
		}
	}

	public static class WrappedRunnable<A, B, I extends Inheriter<A, B>, E extends Throwable, W extends Runnable<? extends E>>
			extends Wrapper<A, B, I, W> implements Runnable<E> {

		public WrappedRunnable(W source, I inheriter) {
			super(source, inheriter);
		}

		@Override
		public void run() throws E {
			B backup = inheriter.replay(capture);
			try {
				this.source.run();
			} finally {
				inheriter.restore(backup);
			}
		}

	}

	default <T, E extends Throwable> Callable<T, E> wrap(@NonNull Callable<? extends T, ? extends E> callable) {
		return new WrappedCallable<>(callable, this);
	}

	default <S, E extends Throwable> Consumer<S, E> wrap(@NonNull Consumer<? super S, ? extends E> consumer) {
		return new WrappedConsumer<>(consumer, this);
	}

	default <S, T, E extends Throwable> Function<S, T, E> wrap(
			@NonNull Function<? super S, ? extends T, ? extends E> function) {
		return new WrappedFunction<>(function, this);
	}

	default <E extends Throwable> Runnable<E> wrap(Runnable<? extends E> runnable) {
		return new WrappedRunnable<>(runnable, this);
	}

}