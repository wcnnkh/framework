package run.soeasy.framework.util.function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @see java.lang.Runnable
 * @author wcnnkh
 *
 * @param <E>
 */
@FunctionalInterface
public interface Runnable<E extends Throwable> {
	public static class NativeRunnable<E extends Throwable> extends Wrapped<java.lang.Runnable> implements Runnable<E> {

		public NativeRunnable(java.lang.Runnable source) {
			super(source);
		}

		@Override
		public void run() throws E {
			source.run();
		}
	}

	@RequiredArgsConstructor
	public static class AndThenRunnable<E extends Throwable> implements Runnable<E> {
		@NonNull
		private final Runnable<? extends E> left;
		@NonNull
		private final Runnable<? extends E> right;

		@Override
		public void run() throws E {
			left.run();
			right.run();
		}
	}

	@RequiredArgsConstructor
	public static class OnCloseRunnable<E extends Throwable> implements Runnable<E> {
		@NonNull
		private final Runnable<? extends E> left;
		@NonNull
		private final Runnable<? extends E> right;

		@Override
		public void run() throws E {
			try {
				left.run();
			} finally {
				right.run();
			}
		}
	}

	@FunctionalInterface
	public static interface ProcessorWrapper<E extends Throwable, W extends Runnable<E>>
			extends Runnable<E>, Wrapper<W> {
		@Override
		default void run() throws E {
			getSource().run();
		}
	}

	public static <E extends Throwable> Runnable<E> forNative(@NonNull java.lang.Runnable runnable) {
		return new NativeRunnable<>(runnable);
	}

	default Runnable<E> andThen(Runnable<? extends E> after) {
		if (after == null) {
			return this;
		}
		return new AndThenRunnable<>(this, after);
	}

	default Runnable<E> onClose(Runnable<? extends E> endpoint) {
		if (endpoint == null) {
			return this;
		}
		return new OnCloseRunnable<>(this, endpoint);
	}

	void run() throws E;
}