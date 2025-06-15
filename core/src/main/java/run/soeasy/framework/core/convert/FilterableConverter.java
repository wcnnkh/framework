package run.soeasy.framework.core.convert;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class FilterableConverter<S, T> extends ConvertFilters<S, T> implements Converter<S, T> {
	@NonNull
	private final Converter<? super S, ? extends T> converter;

	public FilterableConverter(@NonNull Iterable<? extends ConvertFilter<S, T>> filters,
			@NonNull Converter<? super S, ? extends T> converter) {
		super(filters);
		this.converter = converter;
	}

	@Override
	public T convert(S source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return convert(source, sourceTypeDescriptor, targetTypeDescriptor, converter);
	}
}
