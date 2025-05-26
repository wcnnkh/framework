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
		public Object convert(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
				throws ConversionException {
			return source;
		}

		@Override
		public boolean canConvert(TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor) {
			return sourceTypeDescriptor.isAssignableTo(targetTypeDescriptor);
		}

	}

	public static ConversionService identity() {
		return IdentityConversionService.INSTANCE;
	}

	default boolean canConvert(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
		return canConvert(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
	}

	default boolean canConvert(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
		return canConvert(TypeDescriptor.valueOf(sourceClass), targetTypeDescriptor);
	}

	default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
		return canConvert(sourceTypeDescriptor, TypeDescriptor.valueOf(targetClass));
	}

	/**
	 * 是否能直接转换，无需重写此方法
	 * 
	 * @param sourceTypeDescriptor
	 * @param targetTypeDescriptor
	 * @return
	 */
	default boolean canDirectlyConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		if (sourceTypeDescriptor.isAssignableTo(targetTypeDescriptor)) {
			return true;
		}

		if (targetTypeDescriptor.getType() == Object.class) {
			return true;
		}
		return false;
	}

	default Object convert(Object source, @NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return convert(source, TypeDescriptor.valueOf(sourceClass), targetTypeDescriptor);
	}

	@SuppressWarnings("unchecked")
	default <T> T convert(@NonNull Object source, @NonNull Class<? extends T> targetClass) throws ConversionException {
		return (T) convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetClass));
	}

	default Object convert(Object source, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return convert(source, TypeDescriptor.forObject(source), targetTypeDescriptor);
	}

	@SuppressWarnings("unchecked")
	default <T> T convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Class<? extends T> targetClass) throws ConversionException {
		return (T) convert(source, sourceTypeDescriptor, TypeDescriptor.valueOf(targetClass));
	}
}
