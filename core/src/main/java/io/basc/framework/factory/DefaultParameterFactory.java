package io.basc.framework.factory;

import io.basc.framework.core.parameter.ParameterDescriptor;

public interface DefaultParameterFactory {
	Object getDefaultParameter(ParameterDescriptor parameterDescriptor);
}
