package scw.mvc.http;

import scw.core.parameter.ParameterConfig;
import scw.mvc.Channel;
import scw.mvc.parameter.ParameterFilter;
import scw.mvc.parameter.ParameterFilterChain;

public abstract class HttpParameterFilter implements ParameterFilter {

	public Object filter(Channel channel, ParameterConfig parameterConfig, ParameterFilterChain chain)
			throws Throwable {
		if (channel instanceof HttpChannel) {
			return filter((HttpChannel) channel, parameterConfig, chain);
		}
		return chain.doFilter(channel, parameterConfig);
	}

	public abstract Object filter(HttpChannel httpChannel, ParameterConfig parameterConfig,
			ParameterFilterChain chain) throws Throwable;
}
