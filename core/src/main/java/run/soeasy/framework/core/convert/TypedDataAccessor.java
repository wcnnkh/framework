package run.soeasy.framework.core.convert;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface TypedDataAccessor<T> extends TypedData<T>, AccessibleDescriptor {
	public static interface TypedDataAccessorWrapper<T, W extends TypedDataAccessor<T>>
			extends TypedDataAccessor<T>, TypedDataWrapper<T, W>, AccessibleDescriptorWrapper<W> {

		@Override
		default void set(T value) {
			getSource().set(value);
		}

		@Override
		default <R> TypedDataAccessor<R> map(
				@NonNull ThrowingFunction<? super T, ? extends R, ConversionException> mapper) {
			return getSource().map(mapper);
		}
	}

	void set(T value);

	@Override
	<R> TypedDataAccessor<R> map(@NonNull ThrowingFunction<? super T, ? extends R, ConversionException> mapper);
}
