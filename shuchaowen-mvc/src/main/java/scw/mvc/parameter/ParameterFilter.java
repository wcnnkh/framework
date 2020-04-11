package scw.mvc.parameter;

import scw.core.parameter.ParameterDescriptor;
import scw.mvc.Channel;

public interface ParameterFilter {
	Object doFilter(Channel channel, ParameterDescriptor parameterConfig, ParameterFilterChain chain)
			throws Throwable;
}
