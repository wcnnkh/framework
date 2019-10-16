package scw.mvc.parameter;

import scw.core.parameter.ParameterConfig;
import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpParameterFilter implements ParameterFilter {

	public Object filter(Channel channel, ParameterConfig parameterConfig, ParameterFilterChain chain)
			throws Throwable {
		if (channel instanceof HttpChannel) {
			return filter((HttpChannel) channel, parameterConfig, chain);
		}
		return chain.doFilter(channel, parameterConfig);
	}

	protected abstract Object filter(HttpChannel channel, ParameterConfig parameterConfig, ParameterFilterChain chain)
			throws Throwable;
}
