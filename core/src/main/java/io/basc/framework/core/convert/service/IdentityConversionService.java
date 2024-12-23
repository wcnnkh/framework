package io.basc.framework.core.convert.service;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;

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
	public Object convert(Value value, TypeDescriptor requiredTypeDescriptor) throws ConversionException {
		return value.get();
	}

}
