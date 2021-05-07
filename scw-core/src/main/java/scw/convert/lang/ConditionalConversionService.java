package scw.convert.lang;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.utils.ClassUtils;
import scw.json.JSONSupportAccessor;

public abstract class ConditionalConversionService extends JSONSupportAccessor implements ConversionService, ConvertibleConditional {
	
	public boolean canConvert(TypeDescriptor sourceType,
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
