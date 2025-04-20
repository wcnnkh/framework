package run.soeasy.framework.core.convert.support;

import java.util.Collections;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.ConvertiblePair;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exe.Function;

public class ConverterConversionService implements ConditionalConversionService {
	@SuppressWarnings("rawtypes")
	private final Function converter;
	private final Set<ConvertiblePair> convertibleTypes;

	public <S, T> ConverterConversionService(Class<S> sourceType, Class<T> targetType,
			Function<? super S, ? extends T, ? extends Throwable> converter) {
		this.convertibleTypes = Collections.singleton(new ConvertiblePair(sourceType, targetType));
		this.converter = converter;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertibleTypes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(@NonNull Source value, @NonNull TypeDescriptor targetType) throws ConversionException {
		try {
			return converter.apply(value.get());
		} catch (Throwable e) {
			if (e instanceof ConversionException) {
				throw (ConversionException) e;
			}
			throw new ConversionFailedException(value.getTypeDescriptor(), targetType, value.get(), e);
		}
	}

	@Override
	public String toString() {
		return "<" + String.valueOf(converter) + ">" + convertibleTypes.toString();
	}
}
