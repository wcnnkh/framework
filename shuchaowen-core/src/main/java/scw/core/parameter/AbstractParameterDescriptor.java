package scw.core.parameter;

import scw.core.parameter.annotation.DefaultValue;
import scw.core.parameter.annotation.ParameterName;
import scw.core.utils.StringUtils;
import scw.util.value.StringValue;
import scw.util.value.Value;

public abstract class AbstractParameterDescriptor implements ParameterDescriptor {

	public String getDisplayName() {
		ParameterName parameterName = getAnnotatedElement().getAnnotation(ParameterName.class);
		if (parameterName != null && StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
		return getName();
	}

	public Value getDefaultValue() {
		DefaultValue defaultValue = getAnnotatedElement().getAnnotation(DefaultValue.class);
		if (defaultValue == null) {
			return null;
		}

		return new StringValue(defaultValue.value());
	}
}
