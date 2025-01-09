package io.basc.framework.util.function;

import io.basc.framework.util.ObjectUtils;
import lombok.Getter;
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
	@FunctionalInterface
	public static interface ProcessorWrapper<E extends Throwable, W extends Runnable<E>>
			extends Runnable<E>, Wrapper<W> {
		@Override
		default void run() throws E {
			getSource().run();
		}
	}

	@Getter
	@RequiredArgsConstructor
	public static class NativeRunnable<E extends Throwable> implements Runnable<E> {
		@NonNull
		private final java.lang.Runnable runnable;

		@Override
		public void run() throws E {
			runnable.run();
		}

		@Override
		public int hashCode() {
			return runnable.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj instanceof NativeRunnable) {
				NativeRunnable<?> nativeRunnable = (NativeRunnable<?>) obj;
				return ObjectUtils.equals(this.runnable, nativeRunnable.runnable);
			}
			return ObjectUtils.equals(this.runnable, obj);
		}

		@Override
		public String toString() {
			return runnable.toString();
		}
	}

	public static <E extends Throwable> Runnable<E> forNative(@NonNull java.lang.Runnable runnable) {
		return new NativeRunnable<>(runnable);
	}

	void run() throws E;
}