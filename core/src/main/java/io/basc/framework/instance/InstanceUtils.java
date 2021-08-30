package io.basc.framework.instance;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.instance.annotation.PropertyName;
import io.basc.framework.util.StringUtils;

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
