package io.basc.framework.factory;

import io.basc.framework.mapper.ParameterDescriptor;

public interface DefaultParameterFactory {
	Object getDefaultParameter(ParameterDescriptor parameterDescriptor);
}
