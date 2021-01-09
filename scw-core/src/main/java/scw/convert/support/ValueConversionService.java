package scw.convert.support;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.value.AnyValue;
import scw.value.Value;
import scw.value.ValueUtils;

public class ValueConversionService extends AbstractConversionService {
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
		return value.getAsObject(targetType.getResolvableType().getType());
	}
	
	public boolean isSupported(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return ValueUtils.isBaseType(sourceType.getType())
				|| Value.class.isAssignableFrom(sourceType.getType())
				|| ValueUtils.isBaseType(targetType.getType())
				|| targetType.getType() == Value.class || AnyValue.class == targetType.getType();
	}
}
