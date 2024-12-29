package io.basc.framework.core.convert.config;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.ClassUtils;

public interface ConditionalConversionService extends ConversionService, ConvertibleConditional {

	@Override
	default boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		for (ConvertiblePair pair : getConvertibleTypes()) {
			if (ClassUtils.isAssignable(pair.getSourceType(), sourceType.getType())
					&& ClassUtils.isAssignable(pair.getTargetType(), targetType.getType())) {
				return true;
			}
		}
		return false;
	}
}
