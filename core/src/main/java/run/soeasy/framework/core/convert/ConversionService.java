package run.soeasy.framework.core.convert;

import lombok.NonNull;

/**
 * A service interface for type conversion. This is the entry point into the
 * convert system. Call {@link #convert(Object, Class)} to perform a thread-safe
 * type conversion using this system.
 */
public interface ConversionService extends Converter<Object, Object> {
	public static ConversionService identity() {
		return IdentityConversionService.INSTANCE;
	}

	public static ConversionService directly() {
		return DirectlyConversionService.INSTANCE;
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

	@Override
	boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull TypeDescriptor targetTypeDescriptor);

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
