package run.soeasy.framework.core.execution.resolver;

import run.soeasy.framework.core.param.ParameterDescriptorTemplate;
import run.soeasy.framework.core.param.Parameters;

public interface ParameterFactory {
	boolean hasParameters(ParameterDescriptorTemplate template);

	Parameters getParameters(ParameterDescriptorTemplate template);
}
