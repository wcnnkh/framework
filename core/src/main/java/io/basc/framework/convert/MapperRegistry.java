package io.basc.framework.convert;

public interface MapperRegistry<S, E extends Throwable>
		extends ReversibleConverterRegistry<S, E>, ReversibleTransformerRegistry<S, E>, Mapper<S, Object, E> {

	<T> void registerMapper(Class<? extends T> type, Mapper<S, T, ? extends E> mapper);

	<T> Mapper<S, T, E> getMapper(Class<? extends T> type);

	default boolean isMapperRegistred(Class<?> type) {
		return getMapper(type) != null;
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
