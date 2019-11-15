package scw.mvc;

import scw.core.parameter.ParameterConfig;

public interface ParameterFilter {
	Object doFilter(Channel channel, ParameterConfig parameterConfig, ParameterFilterChain chain)
			throws Throwable;
}
