package run.soeasy.framework.core.convert;

class IdentityConverter<T> implements Converter<T, T> {
	static final IdentityConverter<?> INSTANCE = new IdentityConverter<>();

	@Override
	public T convert(T source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return source;
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor) {
		return sourceTypeDescriptor.isAssignableTo(targetTypeDescriptor);
	}

}