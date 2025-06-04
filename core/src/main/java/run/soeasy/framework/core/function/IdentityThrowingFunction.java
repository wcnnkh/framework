package run.soeasy.framework.core.function;

class IdentityThrowingFunction<T, E extends Throwable> implements ThrowingFunction<T, T, E> {
	static final IdentityThrowingFunction<?, ?> INSTANCE = new IdentityThrowingFunction<>();

	@Override
	public T apply(T source) throws E {
		return source;
	}
}