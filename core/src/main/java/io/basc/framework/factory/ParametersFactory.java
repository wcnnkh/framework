package io.basc.framework.factory;

import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptors;
import io.basc.framework.util.Elements;

public interface ParametersFactory {
	boolean isAccept(ParameterDescriptors parameterDescriptors);

	Elements<? extends Parameter> getParameters(ParameterDescriptors parameterDescriptors);
}
