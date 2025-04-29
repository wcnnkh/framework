package run.soeasy.framework.core.function;

public interface RuntimeCloseableSupplier<T, E extends RuntimeException>
		extends Source<T, E>, RuntimeThrowingSupplier<T, E> {

	public static interface RuntimeCloseableSupplierWrapper<T, E extends RuntimeException, W extends RuntimeCloseableSupplier<T, E>>
			extends RuntimeCloseableSupplier<T, E>, SourceWrapper<T, E, W>,
			ThrowingSupplierWrapper<T, E, W> {

		@Override
		default T get() {
			return getSource().get();
		}

	}
}
