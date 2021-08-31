package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.ClassUtils;

public abstract class ConditionalConversionService extends AbstractConversionService
		implements ConversionService, ConvertibleConditional {

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
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
