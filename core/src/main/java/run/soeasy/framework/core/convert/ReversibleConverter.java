package run.soeasy.framework.core.convert;

public interface ReversibleConverter<S, T> extends Converter<S, T> {
	S reverseConvert(T source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor) throws ConversionException;

	default ReversibleConverter<T, S> reversed() {
		return new ReversedConverter<>(this);
	}
}