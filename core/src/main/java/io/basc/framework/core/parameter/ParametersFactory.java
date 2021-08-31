package io.basc.framework.core.parameter;

public interface ParametersFactory {
	boolean isAccept(ParameterDescriptors parameterDescriptors);

	Object[] getParameters(ParameterDescriptors parameterDescriptors);
}