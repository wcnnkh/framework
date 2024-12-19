package io.basc.framework.core.convert.support;

import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.TypeDescriptor;

class ValueConversionService implements ConversionService {

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Value value = Value.of(source, targetType);
		if (targetType.getType() == Value.class) {
			return value;
		}

		return value.getAsObject(targetType);
	}

	private boolean isValueType(Class<?> type, boolean isAssignableFrom) {
		// 用来处理基本数据类型之前的转换 如：int->long
		if (Value.isBaseType(type)) {
			return true;
		}

		if (isAssignableFrom) {
			return Value.class.isAssignableFrom(type);
		} else {
			return Value.class == type;
		}
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		return isValueType(sourceType.getType(), true) || isValueType(targetType.getType(), false);
	}
}
