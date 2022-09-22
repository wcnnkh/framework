package io.basc.framework.factory;

import io.basc.framework.core.parameter.ParameterDescriptors;

public interface ParametersFactory {
	boolean isAccept(ParameterDescriptors parameterDescriptors);

	Object[] getParameters(ParameterDescriptors parameterDescriptors);
}
