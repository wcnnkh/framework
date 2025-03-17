package run.soeasy.framework.core.execution.resolver;

import run.soeasy.framework.core.execution.ParameterDescriptorTemplate;
import run.soeasy.framework.core.execution.Parameters;

public interface ParameterFactory {
	boolean hasParameters(ParameterDescriptorTemplate template);

	Parameters getParameters(ParameterDescriptorTemplate template);
}
