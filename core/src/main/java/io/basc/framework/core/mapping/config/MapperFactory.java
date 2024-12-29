package io.basc.framework.core.mapping.config;

import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConverterFactory;
import io.basc.framework.core.convert.transform.Transformer;
import io.basc.framework.core.convert.transform.config.TransformerFactory;
import io.basc.framework.core.mapping.Mapper;
import lombok.NonNull;

public interface MapperFactory<S, E extends Throwable>
		extends ConverterFactory<S, E>, TransformerFactory<S, E>, Mapper<S, Object, E> {
	<T> Mapper<S, T, E> getMapper(@NonNull Class<? extends T> requiredType);

	@Override
	default boolean canInstantiated(TypeDescriptor type) {
		Mapper<S, ?, E> mapper = getMapper(type.getType());
		return mapper == null ? false : mapper.canInstantiated(type);
	}

	@Override
	default Object newInstance(@NonNull TypeDescriptor requiredType) {
		Mapper<S, ?, E> mapper = getMapper(requiredType.getType());
		if (mapper == null) {
			throw new UnsupportedOperationException(requiredType.toString());
		}
		return mapper.newInstance(requiredType);
	}

	@Override
	default <T> Converter<S, T, E> getConverter(Class<? extends T> requiredType) {
		return getMapper(requiredType);
	}

	@Override
	default <T> Transformer<S, T, E> getTransformer(@NonNull Class<? extends T> requiredType) {
		return getMapper(requiredType);
	}

	@Override
	default boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ConverterFactory.super.canConvert(sourceType, targetType)
				|| Mapper.super.canConvert(sourceType, targetType);
	}

	@Override
	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (ConverterFactory.super.canConvert(sourceType, targetType)) {
			return ConverterFactory.super.convert(source, sourceType, targetType);
		}
		return Mapper.super.convert(source, sourceType, targetType);
	}
}
