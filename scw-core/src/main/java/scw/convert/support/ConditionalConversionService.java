package scw.convert.support;

import scw.convert.TypeDescriptor;
import scw.core.utils.ClassUtils;

public abstract class ConditionalConversionService extends
		AbstractConversionService implements ConvertibleConditional {
	
	public boolean isSupported(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		for (ConvertiblePair pair : getConvertibleTypes()) {
			if ((sourceType == null || ClassUtils.isAssignable(
					pair.getSourceType(), sourceType.getType()))
					&& ClassUtils.isAssignable(pair.getTargetType(), targetType.getType())) {
				return true;
			}
		}
		return false;
	}
}
