package run.soeasy.framework.core.convert;

class IdentityConversionService extends IdentityConverter<Object> implements ConversionService {
	static final ConversionService INSTANCE = new IdentityConversionService();

	@Override
	public boolean canConvert(TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor) {
		return sourceTypeDescriptor.isAssignableTo(targetTypeDescriptor);
	}

}