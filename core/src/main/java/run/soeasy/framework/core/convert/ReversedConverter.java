package run.soeasy.framework.core.convert;

import lombok.NonNull;

public class ReversedConverter<S, T> implements ReversibleConverter<S, T> {
	private Converter<? super S, ? extends T> converter;
	private Converter<? super T, ? extends S> reverseConverter;
	private ReversibleConverter<T, S> reversibleConverter;

	public ReversedConverter(@NonNull Converter<? super S, ? extends T> converter,
			@NonNull Converter<? super T, ? extends S> reverseConverter) {
		this.converter = converter;
		this.reverseConverter = reverseConverter;
	}

	public ReversedConverter(@NonNull ReversibleConverter<T, S> reversibleConverter) {
		this.reversibleConverter = reversibleConverter;
	}

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return (reversibleConverter != null && reverseConverter.canConvert(sourceTypeDescriptor, targetTypeDescriptor))
				|| (converter != null && converter.canConvert(sourceTypeDescriptor, targetTypeDescriptor))
				|| (reverseConverter != null && converter.canConvert(targetTypeDescriptor, sourceTypeDescriptor));
	}

	@Override
	public T convert(S source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (reversibleConverter != null) {
			return reversibleConverter.reverseConvert(source, sourceTypeDescriptor, targetTypeDescriptor);
		}

		if (converter != null) {
			return this.converter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
		}

		throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public S reverseConvert(T source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		if (reversibleConverter != null) {
			return reversibleConverter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
		}

		if (converter != null) {
			return this.reverseConverter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
		}
		throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public ReversibleConverter<T, S> reversed() {
		return reversibleConverter == null ? new ReversedConverter<>(reverseConverter, converter) : reversibleConverter;
	}
}
