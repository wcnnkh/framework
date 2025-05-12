package run.soeasy.framework.core.convert;

public interface TypedDataAccessor<T> extends TypedData<T>, AccessibleDescriptor {
	public static interface TypedDataAccessorWrapper<T, W extends TypedDataAccessor<T>>
			extends TypedDataAccessor<T>, TypedDataWrapper<T, W>, AccessibleDescriptorWrapper<W> {

		@Override
		default void set(T value) {
			getSource().set(value);
		}
	}

	void set(T value);
}
