package run.soeasy.framework.core.convert;

import lombok.NonNull;

@FunctionalInterface
public interface ReversibleConverterFactory<S, E extends Throwable>
		extends ReversibleConverter<S, Object, E>, ConverterFactory<S, E> {

	@Override
	default <T> Converter<S, T, E> getConverter(Class<? extends T> targetType) {
		return getReversibleConverter(targetType);
	}

	<T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> requiredType);

	@SuppressWarnings("unchecked")
	@Override
	default S reverseConvert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return (S) source;
		}

		ReversibleConverter<S, Object, E> converter = getReversibleConverter(sourceType.getType());
		if (converter == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return converter.reverseConvert(source, sourceType, targetType);
	}

	@SuppressWarnings("unchecked")
	default <T> T reverseConvert(@NonNull Object source, @NonNull Class<? extends T> requiredType) throws E {
		return (T) reverseConvert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(requiredType));
	}

	@Override
	default boolean canReverseConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (canDirectlyConvert(sourceType, targetType)) {
			return true;
		}

		ReversibleConverter<S, Object, E> converter = getReversibleConverter(sourceType.getType());
		if (converter == null) {
			return false;
		}
		return converter.canReverseConvert(sourceType, targetType);
	}
}
