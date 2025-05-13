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
public interface ConversionService extends Converter<Object, Object, ConversionException> {
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

	@Override
	boolean canConvert(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType);

	@Override
	Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException;
}
