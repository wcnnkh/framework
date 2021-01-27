package scw.convert.support;

import scw.convert.ConversionFailedException;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.value.AnyValue;
import scw.value.Value;
import scw.value.ValueUtils;

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
		
		try {
			return value.getAsObject(targetType.getResolvableType());
		} catch (Exception e) {
			throw new ConversionFailedException(sourceType, targetType, value, e);
		}
	}
	
	public boolean canConvert(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return ValueUtils.isBaseType(sourceType.getType())
				|| Value.class.isAssignableFrom(sourceType.getType())
				|| ValueUtils.isBaseType(targetType.getType())
				|| targetType.getType() == Value.class || AnyValue.class == targetType.getType();
	}
}
