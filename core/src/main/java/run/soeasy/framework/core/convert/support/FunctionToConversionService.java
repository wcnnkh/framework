package run.soeasy.framework.core.convert.support;

import java.util.Collections;
import java.util.Set;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConditionalConversionService;
import run.soeasy.framework.core.convert.service.ConvertiblePair;
import run.soeasy.framework.core.function.ThrowingFunction;

public class FunctionToConversionService implements ConditionalConversionService {
	private final ThrowingFunction<? super Object, ? extends Object, ? extends Throwable> converter;
	private final Set<ConvertiblePair> convertibleTypes;

	public <S, T> FunctionToConversionService(Class<S> sourceType, Class<T> targetType,
			ThrowingFunction<? super Object, ? extends Object, ? extends Throwable> converter) {
		this.convertibleTypes = Collections.singleton(new ConvertiblePair(sourceType, targetType));
		this.converter = converter;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertibleTypes;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		try {
			return converter.apply(source);
		} catch (Throwable e) {
			if (e instanceof ConversionException) {
				throw (ConversionException) e;
			}
			throw new ConversionFailedException(sourceType, targetType, source, e);
		}
	}

	@Override
	public String toString() {
		return "<" + String.valueOf(converter) + ">" + convertibleTypes.toString();
	}
}
