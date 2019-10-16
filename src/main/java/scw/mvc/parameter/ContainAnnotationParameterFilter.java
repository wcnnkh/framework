package scw.mvc.parameter;

import scw.core.parameter.ContainAnnotationParameterConfig;
import scw.core.parameter.ParameterConfig;
import scw.mvc.Channel;

public abstract class ContainAnnotationParameterFilter implements ParameterFilter {

	public Object filter(Channel channel, ParameterConfig parameterConfig, ParameterFilterChain chain)
			throws Throwable {
		if (parameterConfig instanceof ContainAnnotationParameterConfig) {
			return filter(channel, (ContainAnnotationParameterConfig) parameterConfig, chain);
		}
		return chain.doFilter(channel, parameterConfig);
	}

	protected abstract Object filter(Channel channel, ContainAnnotationParameterConfig parameterConfig,
			ParameterFilterChain chain) throws Throwable;
}
