package run.soeasy.framework.core.convert.value;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface TypedValueAccessorWrapper<W extends TypedValueAccessor>
		extends TypedValueAccessor, TypedValueWrapper<W>, TypedDataAccessorWrapper<Object, W> {
	@Override
	default <R> TypedDataAccessor<R> map(@NonNull Class<R> type, @NonNull Converter converter) {
		return getSource().map(type, converter);
	}

	@Override
	default TypedValueAccessor map(@NonNull TypeDescriptor typeDescriptor, @NonNull Converter converter) {
		return getSource().map(typeDescriptor, converter);
	}
}