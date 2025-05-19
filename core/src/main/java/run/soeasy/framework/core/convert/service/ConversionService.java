package run.soeasy.framework.core.convert.service;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * A service interface for type conversion. This is the entry point into the
 * convert system. Call {@link #convert(Object, Class)} to perform a thread-safe
 * type conversion using this system.
 */
public interface ConversionService extends Convertible, Converter<Object, Object> {
	public static class IdentityConversionService implements ConversionService {
		private static final ConversionService INSTANCE = new IdentityConversionService();

		@Override
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
				throws ConversionException {
			return source;
		}

		@Override
		public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
			return sourceType.isAssignableTo(targetType);
		}

	}

	public static ConversionService identity() {
		return IdentityConversionService.INSTANCE;
	}

	default boolean canConvert(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
		return canConvert(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
	}

	default boolean canConvert(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetType) {
		return canConvert(TypeDescriptor.valueOf(sourceClass), targetType);
	}

	default boolean canConvert(@NonNull TypeDescriptor sourceType, @NonNull Class<?> targetClass) {
		return canConvert(sourceType, TypeDescriptor.valueOf(targetClass));
	}

	/**
	 * 是否能直接转换，无需重写此方法
	 * 
	 * @param sourceType
	 * @param targetType
	 * @return
	 */
	default boolean canDirectlyConvert(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		if (sourceType.isAssignableTo(targetType)) {
			return true;
		}

		if (targetType.getType() == Object.class) {
			return true;
		}
		return false;
	}

	default Object convert(Object source, @NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetType)
			throws ConversionException {
		return convert(source, TypeDescriptor.valueOf(sourceClass), targetType);
	}

	@SuppressWarnings("unchecked")
	default <T> T convert(@NonNull Object source, @NonNull Class<? extends T> targetClass) throws ConversionException {
		return (T) convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetClass));
	}

	default Object convert(Object source, @NonNull TypeDescriptor targetType) throws ConversionException {
		return convert(source, TypeDescriptor.forObject(source), targetType);
	}

	@SuppressWarnings("unchecked")
	default <T> T convert(Object source, @NonNull TypeDescriptor sourceType, @NonNull Class<? extends T> targetClass)
			throws ConversionException {
		return (T) convert(source, sourceType, TypeDescriptor.valueOf(targetClass));
	}
}
