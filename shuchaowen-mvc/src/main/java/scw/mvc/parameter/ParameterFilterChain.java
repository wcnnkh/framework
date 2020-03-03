package scw.mvc.parameter;

import scw.core.parameter.ParameterConfig;
import scw.mvc.Channel;

public interface ParameterFilterChain {
	Object doFilter(Channel channel, ParameterConfig parameterConfig) throws Throwable;
}