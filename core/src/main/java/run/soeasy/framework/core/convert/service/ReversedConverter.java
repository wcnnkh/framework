package run.soeasy.framework.core.convert.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@RequiredArgsConstructor
class ReversedConverter<S, T, W extends ReversibleConverter<T, S>> implements ReversibleConverter<S, T> {
	@NonNull
	private final W source;

	@Override
	public T convert(S source, @NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType)
			throws ConversionException {
		return this.source.reverseConvert(source, sourceType, targetType);
	}

	@Override
	public S reverseConvert(T source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return this.source.convert(source, sourceType, targetType);
	}

	@Override
	public ReversibleConverter<T, S> reversed() {
		return source;
	}
}
