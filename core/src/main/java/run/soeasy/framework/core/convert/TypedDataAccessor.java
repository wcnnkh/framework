package run.soeasy.framework.core.convert;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface TypedDataAccessor<T> extends TypedData<T>, Accessor {
	public static interface TypedDataAccessorWrapper<T, W extends TypedDataAccessor<T>>
			extends TypedDataAccessor<T>, TypedDataWrapper<T, W>, AccessibleWrapper<W> {
		@Override
		default boolean isReadable() {
			return getSource().isReadable();
		}

		@Override
		default boolean isWriteable() {
			return getSource().isWriteable();
		}

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

	/**
	 * 是否可读
	 * 
	 * @return
	 */
	default boolean isReadable() {
		return true;
	}

	/**
	 * 是否可写
	 * 
	 * @return
	 */
	boolean isWriteable();

	void set(T value);

	@Override
	<R> TypedDataAccessor<R> map(@NonNull ThrowingFunction<? super T, ? extends R, ConversionException> mapper);
}
