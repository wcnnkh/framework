package run.soeasy.framework.core.convert;

import lombok.NonNull;

@FunctionalInterface
public interface Converter extends Convertable {
	public static Converter assignable() {
		return AssignableConverter.INSTANCE;
	}

	@Override
	default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return true;
	}

	@SuppressWarnings("unchecked")
	default <T> T convert(@NonNull Object source, @NonNull Class<? extends T> targetClass) throws ConversionException {
		return (T) convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetClass));
	}

	default Object convert(Object source, @NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return convert(source, TypeDescriptor.valueOf(sourceClass), targetTypeDescriptor);
	}

	default Object convert(Object source, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return convert(source, TypeDescriptor.forObject(source), targetTypeDescriptor);
	}

	@SuppressWarnings("unchecked")
	default <T> T convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Class<? extends T> targetClass) throws ConversionException {
		return (T) convert(source, sourceTypeDescriptor, TypeDescriptor.valueOf(targetClass));
	}

	Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException;
}