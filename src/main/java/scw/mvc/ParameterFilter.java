package scw.mvc;

import scw.core.parameter.ParameterConfig;

public interface ParameterFilter {
	Object filter(Channel channel, ParameterConfig parameterConfig, ParameterFilterChain chain)
			throws Throwable;
}
