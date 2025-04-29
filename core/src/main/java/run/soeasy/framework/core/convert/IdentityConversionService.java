package run.soeasy.framework.core.convert;

public class IdentityConversionService implements ConversionService {

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
	public Object apply(TypedValue accessor, TypeDescriptor requiredTypeDescriptor) throws ConversionException {
		return accessor.get();
	}

}
