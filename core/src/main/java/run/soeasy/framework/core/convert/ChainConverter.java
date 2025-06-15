package run.soeasy.framework.core.convert;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ChainConverter<S, T> implements Converter<S, T> {
	@NonNull
	private final Iterator<? extends ConvertFilter<S, T>> iterator;
	private Converter<? super S, ? extends T> converter;

	@Override
	public T convert(S source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (iterator.hasNext()) {
			return iterator.next().convert(source, sourceTypeDescriptor, targetTypeDescriptor, this);
		} else if (converter != null) {
			return converter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
		}
		throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
	}
}
