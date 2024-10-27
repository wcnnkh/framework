package io.basc.framework.convert.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.DefaultObjectValue;
import io.basc.framework.convert.lang.ObjectValue;

class ValueConversionService implements ConversionService {

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		ObjectValue value = new DefaultObjectValue(source, sourceType);
		if (targetType.getType() == ObjectValue.class) {
			return value;
		}

		return value.getAsObject(targetType);
	}

	private boolean isValueType(Class<?> type, boolean isAssignableFrom) {
		// 用来处理基本数据类型之前的转换 如：int->long
		if (ObjectValue.isBaseType(type)) {
			return true;
		}

		if (isAssignableFrom) {
			return ObjectValue.class.isAssignableFrom(type);
		} else {
			return ObjectValue.class == type;
		}
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		return isValueType(sourceType.getType(), true) || isValueType(targetType.getType(), false);
	}
}
