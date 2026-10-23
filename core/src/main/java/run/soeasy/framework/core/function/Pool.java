package run.soeasy.framework.core.function;

import lombok.NonNull;

public interface Pool<T, E extends Exception> extends ThrowingSupplier<T, E> {
	void close(@NonNull T source) throws E;
}
