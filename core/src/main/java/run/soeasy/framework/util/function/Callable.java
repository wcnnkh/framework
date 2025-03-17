package run.soeasy.framework.util.function;

import lombok.NonNull;

/**
 * 和Source有点结构一样，但意义不同，表示一个回调
 * 
 * @author shuchaowen
 *
 * @param <V>
 * @param <E>
 */
@FunctionalInterface
public interface Callable<V, E extends Throwable> {
	public static class NativeCallable<T> extends Wrapped<java.util.concurrent.Callable<? extends T>>
			implements Callable<T, Exception> {

		public NativeCallable(java.util.concurrent.Callable<? extends T> source) {
			super(source);
		}

		@Override
		public T call() throws Exception {
			return this.source.call();
		}
	}

	public static <T> Callable<T, Exception> forNative(@NonNull java.util.concurrent.Callable<? extends T> callable) {
		return new NativeCallable<>(callable);
	}

	V call() throws E;
}
