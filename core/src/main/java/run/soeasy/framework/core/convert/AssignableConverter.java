package run.soeasy.framework.core.convert;

class AssignableConverter implements Converter {
	static final AssignableConverter INSTANCE = new AssignableConverter();

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