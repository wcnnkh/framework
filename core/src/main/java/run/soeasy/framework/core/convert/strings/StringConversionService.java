package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;

public final class StringConversionService implements ConversionService {
	public static final StringConversionService DEFAULT = new StringConversionService();

	private final StringConverter stringConverter;

	public StringConversionService() {
		this(StringConverter.getInstance());
	}

	public StringConversionService(StringConverter stringConverter) {
		Assert.requiredArgument(stringConverter != null, "stringConverter");
		this.stringConverter = stringConverter;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (sourceType.getType() == String.class) {
			return stringConverter.convert((String) source, sourceType, targetType);
		} else if (targetType.getType() == String.class) {
			return stringConverter.reverseConvert(source, sourceType, targetType);
		}
		throw new ConversionFailedException(sourceType, targetType, source, null);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		if (sourceType.getType() == String.class) {
			return stringConverter.canConvert(sourceType, targetType);
		} else if (targetType.getType() == String.class) {
			return stringConverter.canReverseConvert(sourceType, targetType);
		}
		return false;
	}
}
