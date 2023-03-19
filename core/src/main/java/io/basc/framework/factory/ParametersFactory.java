package io.basc.framework.factory;

import io.basc.framework.mapper.ParameterDescriptors;

public interface ParametersFactory {
	boolean isAccept(ParameterDescriptors parameterDescriptors);

	Object[] getParameters(ParameterDescriptors parameterDescriptors);
}
