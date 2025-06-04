package run.soeasy.framework.core.function;

class IgnoreThrowingConsumer<S, E extends Throwable> implements ThrowingConsumer<S, E> {
	static final IgnoreThrowingConsumer<?, ?> INSTANCE = new IgnoreThrowingConsumer<>();

	@Override
	public void accept(S source) throws E {
	}
}