package run.soeasy.framework.core.function.lang;

public interface RuntimeThrowingRunnableWrapper<E extends RuntimeException, W extends RuntimeThrowingRunnable<E>>
		extends RuntimeThrowingRunnable<E>, ThrowingRunnableWrapper<E, W> {

	@Override
	default void run() {
		getSource().run();
	}

}