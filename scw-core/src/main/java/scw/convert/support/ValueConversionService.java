package scw.convert.support;

import scw.convert.TypeDescriptor;
import scw.value.AnyValue;
import scw.value.Value;
import scw.value.ValueUtils;

public class ValueConversionService extends AbstractConversionService {

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		AnyValue value = new AnyValue(source);
		return value.getAsObject(targetType.getResolvableType().getType());
	}

	public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
		return isValueType(sourceType) && isValueType(targetType);
	}

	private boolean isValueType(Class<?> type) {
		return ValueUtils.isBaseType(type) || Value.class == type;
	}
}
