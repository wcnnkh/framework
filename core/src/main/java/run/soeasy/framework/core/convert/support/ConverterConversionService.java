package run.soeasy.framework.core.convert.support;

import java.util.Collections;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.ConvertiblePair;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.core.convert.TypedValue;
import run.soeasy.framework.core.function.ThrowingFunction;

public class ConverterConversionService implements ConditionalConversionService {
	private final ThrowingFunction<? super Object, ? extends Object, ? extends Throwable> converter;
	private final Set<ConvertiblePair> convertibleTypes;

	public <S, T> ConverterConversionService(Class<S> sourceType, Class<T> targetType,
			ThrowingFunction<? super Object, ? extends Object, ? extends Throwable> converter) {
		this.convertibleTypes = Collections.singleton(new ConvertiblePair(sourceType, targetType));
		this.converter = converter;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertibleTypes;
	}

	@Override
	public Object apply(@NonNull TypedValue value, @NonNull TargetDescriptor targetDescriptor) {
		try {
			return converter.apply(value.get());
		} catch (Throwable e) {
			if (e instanceof ConversionException) {
				throw (ConversionException) e;
			}
			throw new ConversionFailedException(value.getReturnTypeDescriptor(),
					targetDescriptor.getRequiredTypeDescriptor(), value.get(), e);
		}
	}

	@Override
	public String toString() {
		return "<" + String.valueOf(converter) + ">" + convertibleTypes.toString();
	}
}
