package run.soeasy.framework.core.convert.registry;

import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class Converters<S, T, E extends Throwable, C extends Converter<? super S, ? extends T, ? extends E>>
		extends ConfigurableServices<C> implements Converter<S, T, E> {

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (C converter : this) {
			if (converter.canConvert(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		for (C converter : this) {
			if (converter.canConvert(sourceType, targetType)) {
				return converter.convert(source, sourceType, targetType);
			}
		}
		throw new ConverterNotFoundException(sourceType, targetType);
	}

}
