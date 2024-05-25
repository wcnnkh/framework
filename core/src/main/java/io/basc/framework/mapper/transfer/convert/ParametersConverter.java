package io.basc.framework.mapper.transfer.convert;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.support.DefaultReversibleConverterRegistry;
import io.basc.framework.execution.Parameters;

public class ParametersConverter extends DefaultReversibleConverterRegistry<Parameters, ConversionException> {
	@Override
	public Object convert(Parameters source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if(super.canConvert(sourceType, targetType)) {
			return super.convert(source, sourceType, targetType);
		}
		
	}
	
	@Override
	public Parameters reverseConvert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if(super.canConvert(sourceType, targetType)) {
			return super.reverseConvert(source, sourceType, targetType);
		}
		return super.reverseConvert(source, sourceType, targetType);
	}
}
