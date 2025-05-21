package run.soeasy.framework.core.function;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ValueThrowingSupplier<T, E extends Throwable> implements ThrowingSupplier<T, E>, Serializable {
	private static final long serialVersionUID = 1L;
	protected final T value;

	@Override
	public T get() throws E {
		return value;
	}
}