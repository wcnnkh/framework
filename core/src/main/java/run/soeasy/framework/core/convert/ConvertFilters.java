package run.soeasy.framework.core.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ConvertFilters<S, T> implements ConvertFilter<S, T> {
	@NonNull
	private final Iterable<? extends ConvertFilter<S, T>> filters;

	@Override
	public T convert(S source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor, Converter<? super S, ? extends T> converter)
			throws ConversionException {
		ChainConverter<S, T> chainConverter = new ChainConverter<>(filters.iterator(), converter);
		return chainConverter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}

}
