package scw.mvc;

import scw.core.reflect.ParameterConfig;

public interface ParameterFilter {
	Object filter(Channel channel, ParameterConfig parameterConfig, ParameterFilterChain chain)
			throws Throwable;
}
