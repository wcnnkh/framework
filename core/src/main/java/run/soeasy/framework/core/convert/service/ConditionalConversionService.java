package run.soeasy.framework.core.convert.service;

import run.soeasy.framework.core.convert.TypeDescriptor;

public interface ConditionalConversionService extends ConversionService, ConvertibleConditional {

	@Override
	default boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		for (ConvertiblePair pair : getConvertibleTypes()) {
			if (pair.canConvert(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}
}
