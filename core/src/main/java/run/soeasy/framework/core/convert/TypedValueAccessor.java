package run.soeasy.framework.core.convert;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface TypedValueAccessor extends TypedValue, TypedDataAccessor<Object> {
	public static interface TypedValueAccessorWrapper<W extends TypedValueAccessor>
			extends TypedValueWrapper<W>, TypedDataAccessorWrapper<Object, W> {
		@Override
		default <R> TypedDataAccessor<R> map(
				@NonNull ThrowingFunction<? super Object, ? extends R, ConversionException> mapper) {
			return getSource().map(mapper);
		}
	}

	@Override
	default <R> TypedDataAccessor<R> map(
			@NonNull ThrowingFunction<? super Object, ? extends R, ConversionException> mapper) {
		Data<R> value = new Data<>();
		value.setObject(this);
		value.setMapper(mapper);
		return value;
	}
}
