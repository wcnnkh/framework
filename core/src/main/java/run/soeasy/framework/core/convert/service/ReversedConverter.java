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
	public T convert(S source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return this.source.reverseConvert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public S reverseConvert(T source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return this.source.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public ReversibleConverter<T, S> reversed() {
		return source;
	}
}
