package run.soeasy.framework.core.convert.value;

public interface TypedDataAccessorWrapper<T, W extends TypedDataAccessor<T>>
		extends TypedDataAccessor<T>, TypedDataWrapper<T, W>, AccessibleDescriptorWrapper<W> {

	@Override
	default void set(T value) {
		getSource().set(value);
	}
}