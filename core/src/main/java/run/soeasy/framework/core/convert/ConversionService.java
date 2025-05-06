package run.soeasy.framework.core.convert;

import java.util.function.BiFunction;

import lombok.NonNull;

/**
 * A service interface for type conversion. This is the entry point into the
 * convert system. Call {@link #convert(Object, Class)} to perform a thread-safe
 * type conversion using this system.
 */
public interface ConversionService
		extends Converter<Object, Object, ConversionException>, BiFunction<TypedValue, TargetDescriptor, Object> {
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

		@Override
		public Object apply(TypedValue value, TargetDescriptor targetDescriptor) {
			return value.get();
		}
	}

	public static ConversionService identity() {
		return IdentityConversionService.INSTANCE;
	}

	@Override
	boolean canConvert(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType);

	@Override
	default Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return apply(TypedValue.of(source, sourceType), AccessibleDescriptor.forTypeDescriptor(targetType));
	}

	@Override
	Object apply(@NonNull TypedValue value, @NonNull TargetDescriptor targetDescriptor);
}
