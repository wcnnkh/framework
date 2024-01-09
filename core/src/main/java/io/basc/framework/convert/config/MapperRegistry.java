package io.basc.framework.convert.config;

import io.basc.framework.convert.Mapper;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;

public interface MapperRegistry<S, E extends Throwable>
		extends ReversibleConverterRegistry<S, E>, ReversibleTransformerRegistry<S, E>, Mapper<S, Object, E> {

	<T> void registerMapper(Class<? extends T> type, Mapper<S, T, ? extends E> mapper);

	<T> Mapper<S, T, E> getMapper(Class<? extends T> type);

	default boolean isMapperRegistred(Class<?> type) {
		return getMapper(type) != null;
	}

	@Override
	default boolean canInstantiated(ResolvableType type) {
		if (isMapperRegistred(type.getRawClass())) {
			return getMapper(type.getRawClass()).canInstantiated(type);
		}
		return Mapper.super.canInstantiated(type);
	}

	@Override
	default Object newInstance(ResolvableType type) {
		if (isMapperRegistred(type.getRawClass())) {
			return getMapper(type.getRawClass()).newInstance(type);
		}
		return Mapper.super.newInstance(type);
	}

	@Override
	default boolean canInstantiated(TypeDescriptor type) {
		if (isMapperRegistred(type.getType())) {
			return getMapper(type.getType()).canInstantiated(type);
		}
		return Mapper.super.canInstantiated(type);
	}

	@Override
	default boolean canInstantiated(Class<?> type) {
		if (isMapperRegistred(type)) {
			return getMapper(type).canInstantiated(type);
		}
		return Mapper.super.canInstantiated(type);
	}

	@Override
	default <T> T newInstance(Class<T> type) {
		if (isMapperRegistred(type)) {
			return getMapper(type).newInstance(type);
		}
		return Mapper.super.newInstance(type);
	}

	@Override
	default Object newInstance(TypeDescriptor type) {
		if (isMapperRegistred(type.getType())) {
			return getMapper(type.getType()).newInstance(type);
		}
		return Mapper.super.newInstance(type);
	}

	@Override
	default S invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (isInverterRegistred(targetType.getType())) {
			return ReversibleConverterRegistry.super.invert(source, sourceType, targetType);
		} else {
			return Mapper.super.invert(source, sourceType, targetType);
		}
	}

	@Override
	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (isConverterRegistred(targetType.getType())) {
			return ReversibleConverterRegistry.super.convert(source, sourceType, targetType);
		} else {
			return Mapper.super.convert(source, sourceType, targetType);
		}
	}
}
