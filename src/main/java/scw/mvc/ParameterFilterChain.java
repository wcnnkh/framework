package scw.mvc;

import scw.core.parameter.ParameterConfig;

public interface ParameterFilterChain {
	Object doFilter(Channel channel, ParameterConfig parameterConfig) throws Throwable;
}