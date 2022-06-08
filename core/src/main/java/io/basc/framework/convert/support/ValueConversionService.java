package io.basc.framework.convert.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

class ValueConversionService implements ConversionService {
	private final ConversionService conversionService;

	public ValueConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@SuppressWarnings("unchecked")
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		AnyValue value = new AnyValue(source, conversionService);
		if (targetType.getType() == Value.class || targetType.getType() == AnyValue.class) {
			return value;
		}

		return value.getAsObject(targetType);
	}

	private boolean isValueType(Class<?> type, boolean isAssignableFrom) {
		if (Value.isBaseType(type)) {
			return true;
		}

		if (isAssignableFrom) {
			return Value.class.isAssignableFrom(type);
		} else {
			return Value.class == type || AnyValue.class == type;
		}
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		return isValueType(sourceType.getType(), true) || isValueType(targetType.getType(), false);
	}
}
