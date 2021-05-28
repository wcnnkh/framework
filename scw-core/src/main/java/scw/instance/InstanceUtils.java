package scw.instance;

import scw.core.parameter.ParameterDescriptor;
import scw.core.utils.StringUtils;
import scw.instance.annotation.PropertyName;

public final class InstanceUtils {
	private InstanceUtils() {
	};

	public static String getPropertyName(ParameterDescriptor parameterDescriptor) {
		PropertyName parameterName = parameterDescriptor
				.getAnnotation(PropertyName.class);
		if (parameterName != null
				&& StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
		return parameterDescriptor.getName();
	}
}
