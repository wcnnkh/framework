package run.soeasy.framework.core.convert.support;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;

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
	public Object convert(Source value, TypeDescriptor requiredTypeDescriptor) throws ConversionException {
		return value.get();
	}

}
