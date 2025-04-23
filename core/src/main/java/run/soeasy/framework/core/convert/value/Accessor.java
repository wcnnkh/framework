package run.soeasy.framework.core.convert.value;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.function.ThrowingOptional;

public interface Accessor<T> extends Accessible, ThrowingOptional<T, ConversionException> {
	public static interface AccessorWrapper<T, W extends Accessor<T>>
			extends Accessor<T>, AccessibleWrapper<W>, ThrowingOptionalWrapper<T, ConversionException, W> {
		@Override
		default boolean isPresent() throws ConversionException {
			return getSource().isPresent();
		}

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
		default <R> Accessor<R> map(@NonNull ThrowingFunction<? super T, ? extends R, ConversionException> mapper) {
			return getSource().map(mapper);
		}
	}

	@Override
	default boolean isPresent() throws ConversionException {
		return ThrowingOptional.super.isPresent();
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
	<R> Accessor<R> map(@NonNull ThrowingFunction<? super T, ? extends R, ConversionException> mapper);
}
