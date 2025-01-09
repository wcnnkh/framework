package io.basc.framework.core.execution.resolver;

import io.basc.framework.core.execution.ParameterDescriptorTemplate;
import io.basc.framework.core.execution.Parameters;

public interface ParameterFactory {
	boolean hasParameters(ParameterDescriptorTemplate template);

	Parameters getParameters(ParameterDescriptorTemplate template);
}
