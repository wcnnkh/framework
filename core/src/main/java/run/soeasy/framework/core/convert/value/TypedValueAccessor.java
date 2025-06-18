package run.soeasy.framework.core.convert.value;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface TypedValueAccessor extends TypedValue, TypedDataAccessor<Object> {

	@SuppressWarnings("unchecked")
	@Override
	default <R> TypedDataAccessor<R> map(@NonNull Class<R> type, @NonNull Converter converter) {
		return (TypedDataAccessor<R>) map(TypeDescriptor.valueOf(type), converter);
	}

	@Override
	default TypedValueAccessor map(@NonNull TypeDescriptor typeDescriptor, @NonNull Converter converter) {
		return new MappedTypedValueAccessor<>(this, typeDescriptor, converter);
	}
}
