package run.soeasy.framework.core.function;

import lombok.NonNull;

public class ValueThrowingOptional<T, E extends Throwable> extends ValueThrowingSupplier<T, E>
		implements ThrowingOptional<T, E> {
	private static final long serialVersionUID = 1L;
	static final ValueThrowingOptional<?, ?> EMPTY = new ValueThrowingOptional<>(null);

	public ValueThrowingOptional(T value) {
		super(value);
	}

	@Override
	public T get() throws E {
		return ThrowingOptional.super.get();
	}

	@Override
	public <R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
			throws E, X {
		return mapper.apply(getValue());
	}
}