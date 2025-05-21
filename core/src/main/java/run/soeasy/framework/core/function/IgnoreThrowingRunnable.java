package run.soeasy.framework.core.function;

class IgnoreThrowingRunnable<E extends Throwable> implements ThrowingRunnable<E> {
	static final IgnoreThrowingRunnable<?> INSTANCE = new IgnoreThrowingRunnable<>();

	@Override
	public void run() throws E {
	}
}