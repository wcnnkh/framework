package scw.mvc;

import scw.core.reflect.ParameterConfig;

public interface ParameterFilterChain {
	Object doFilter(Channel channel, ParameterConfig parameterConfig) throws Throwable;
}