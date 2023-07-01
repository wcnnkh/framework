package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

public interface ReversibleConverterRegistry<S, E extends Throwable>
		extends ConverterRegistry<S, E>, InverterRegistry<S, E>, ReversibleConverter<S, Object, E> {

	default boolean canDirectlyConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType != null && targetType != null && targetType.isAssignableTo(sourceType)) {
			return true;
		}

		if (targetType.getType() == Object.class) {
			return true;
		}
		return false;
	}

	default boolean isReversibleConverterRegistred(Class<?> type) {
		return getReversibleConverter(type) != null;
	}

	<T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> type);

	<T> void registerReversibleConverter(Class<T> type, ReversibleConverter<S, T, ? extends E> reversibleConverter);

	@Override
	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return source;
		}
		return ConverterRegistry.super.convert(source, sourceType, targetType);
	}

	@SuppressWarnings("unchecked")
	@Override
	default S invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return (S) source;
		}
		return InverterRegistry.super.invert(source, sourceType, targetType);
	}
}