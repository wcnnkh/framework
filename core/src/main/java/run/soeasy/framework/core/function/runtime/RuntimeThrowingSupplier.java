package run.soeasy.framework.core.function.runtime;

import java.util.function.Supplier;

import run.soeasy.framework.core.function.ThrowingSupplier;

public interface RuntimeThrowingSupplier<T, E extends RuntimeException> extends ThrowingSupplier<T, E>, Supplier<T> {
	@FunctionalInterface
	public static interface RuntimeThrowingSupplierWrapper<T, E extends RuntimeException, W extends RuntimeThrowingSupplier<T, E>>
			extends RuntimeThrowingSupplier<T, E>, ThrowingSupplierWrapper<T, E, W> {

		@Override
		default T get() {
			return getSource().get();
		}
	}
}