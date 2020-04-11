package scw.mvc.parameter;

import scw.core.parameter.ParameterDescriptor;
import scw.mvc.Channel;

public interface ParameterFilterChain {
	Object doFilter(Channel channel, ParameterDescriptor parameterConfig) throws Throwable;
}