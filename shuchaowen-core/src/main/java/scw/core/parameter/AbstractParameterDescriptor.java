package scw.core.parameter;

import scw.core.annotation.ParameterName;
import scw.core.utils.StringUtils;

public abstract class AbstractParameterDescriptor implements ParameterDescriptor {

	public String getDisplayName() {
		ParameterName parameterName = getAnnotatedElement().getAnnotation(ParameterName.class);
		if (parameterName != null && StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
		return getName();
	}
}
