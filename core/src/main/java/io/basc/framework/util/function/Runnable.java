package io.basc.framework.util.function;

import lombok.NonNull;

/**
 * @see java.lang.Runnable
 * @author wcnnkh
 *
 * @param <E>
 */
@FunctionalInterface
public interface Runnable<E extends Throwable> {
	@FunctionalInterface
	public static interface ProcessorWrapper<E extends Throwable, W extends Runnable<E>>
			extends Runnable<E>, Wrapper<W> {
		@Override
		default void run() throws E {
			getSource().run();
		}
	}

	public static class NativeRunnable<E extends Throwable> extends Wrapped<java.lang.Runnable> implements Runnable<E> {

		public NativeRunnable(java.lang.Runnable source) {
			super(source);
		}

		@Override
		public void run() throws E {
			source.run();
		}
	}

	public static <E extends Throwable> Runnable<E> forNative(@NonNull java.lang.Runnable runnable) {
		return new NativeRunnable<>(runnable);
	}

	void run() throws E;
}