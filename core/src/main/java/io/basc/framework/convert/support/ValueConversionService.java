package io.basc.framework.convert.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueUtils;

class ValueConversionService implements ConversionService {
	private final ConversionService conversionService;

	public ValueConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		AnyValue value = new AnyValue(source, conversionService);
		if(targetType.getType() == Value.class || targetType.getType() == AnyValue.class){
			return value;
		}
		
		return value.getAsObject(targetType);
	}
	
	public boolean canConvert(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(sourceType == null || targetType == null) {
			return false;
		}
		
		return ValueUtils.isBaseType(sourceType.getType())
				|| Value.class.isAssignableFrom(sourceType.getType())
				|| ValueUtils.isBaseType(targetType.getType())
				|| targetType.getType() == Value.class || AnyValue.class == targetType.getType();
	}
}
