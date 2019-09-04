package scw.mvc.http;

import scw.mvc.Channel;
import scw.mvc.ParameterDefinition;
import scw.mvc.ParameterFilter;
import scw.mvc.ParameterFilterChain;

public abstract class HttpParameterFilter implements ParameterFilter {

	public Object filter(Channel channel, ParameterDefinition parameterDefinition, ParameterFilterChain chain)
			throws Throwable {
		if (channel instanceof HttpChannel) {
			return filter((HttpChannel) channel, parameterDefinition, chain);
		}
		return chain.doFilter(channel, parameterDefinition);
	}

	public abstract Object filter(HttpChannel httpChannel, ParameterDefinition parameterDefinition,
			ParameterFilterChain chain) throws Throwable;
}
