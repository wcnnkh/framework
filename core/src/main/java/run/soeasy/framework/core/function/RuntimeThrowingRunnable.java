package run.soeasy.framework.core.function;

public interface RuntimeThrowingRunnable<E extends RuntimeException> extends ThrowingRunnable<E>, Runnable {
	public static interface RuntimeThrowingRunnableWrapper<E extends RuntimeException, W extends RuntimeThrowingRunnable<E>>
			extends RuntimeThrowingRunnable<E>, ThrowingRunnableWrapper<E, W> {

		@Override
		default void run() {
			getSource().run();
		}

	}
}
