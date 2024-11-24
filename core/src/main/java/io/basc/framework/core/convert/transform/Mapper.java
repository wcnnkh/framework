package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.ConversionFailedException;
import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.stractegy.InstanceFactory;

public interface Mapper<S, T, E extends Throwable> extends Converter<S, T, E>, Transformer<S, T, E>, InstanceFactory {
	@Override
	default boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return canInstantiated(targetType) && canTransform(sourceType, targetType);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws E, ConversionFailedException {
		if (this.canConvert(sourceType, targetType)) {
			T target = (T) newInstance(targetType);
			transform(source, sourceType, target, targetType);
			return target;
		}
		throw new ConversionFailedException(sourceType, targetType, source, null);
	}
}