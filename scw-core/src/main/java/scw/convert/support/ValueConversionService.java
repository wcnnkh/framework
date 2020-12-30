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

	public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
		return ValueUtils.isBaseType(sourceType)
				|| Value.class.isAssignableFrom(sourceType)
				|| ValueUtils.isBaseType(targetType)
				|| targetType == Value.class || AnyValue.class == targetType;
	}
}
