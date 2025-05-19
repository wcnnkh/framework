package run.soeasy.framework.core.convert.service;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface ReversibleConverter<S, T> extends Converter<S, T> {
	S reverseConvert(T source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException;

	default ReversibleConverter<T, S> reversed() {
		return new ReversedConverter<>(this);
	}
}