package scw.configure.support;

import scw.convert.TypeDescriptor;
import scw.convert.support.ConvertibleConditional;
import scw.convert.support.ConvertiblePair;
import scw.core.utils.ClassUtils;

public abstract class ConditionalConfigure extends AbstractConfigure implements
		ConvertibleConditional {

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConvertiblePair pair : getConvertibleTypes()) {
			if ((sourceType == null || ClassUtils.isAssignable(
					pair.getSourceType(), sourceType.getType()))
					&& ClassUtils.isAssignable(pair.getTargetType(),
							targetType.getType())) {
				return true;
			}
		}
		return false;
	}
}
