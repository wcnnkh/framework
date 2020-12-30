package scw.convert.support;

import scw.core.utils.ClassUtils;

public abstract class ConditionalConversionService extends
		AbstractConversionService implements ConvertibleConditional {
	public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
		for (ConvertiblePair pair : getConvertibleTypes()) {
			if ((sourceType == null || ClassUtils.isAssignable(pair.getSourceType(), sourceType))
					&& ClassUtils
							.isAssignable(pair.getTargetType(), targetType)) {
				return true;
			}
		}
		return false;
	}
}
