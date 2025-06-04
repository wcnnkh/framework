package run.soeasy.framework.core.function;

class EmptyPipeline<T, E extends Throwable> implements Pipeline<T, E> {
	static final Pipeline<?, ?> INSTANCE = new EmptyPipeline<>();

	@Override
	public T get() throws E {
		return null;
	}

	@Override
	public boolean isClosed() {
		return true;
	}

	@Override
	public void close() throws E {
	}
}
