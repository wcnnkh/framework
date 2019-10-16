package scw.mvc.http;

import scw.core.parameter.ContainAnnotationParameterConfig;
import scw.mvc.Channel;
import scw.mvc.parameter.ParameterFilter;
import scw.mvc.parameter.ParameterFilterChain;

public abstract class HttpParameterFilter implements ParameterFilter {

	public Object filter(Channel channel, ContainAnnotationParameterConfig containAnnotationParameterConfig, ParameterFilterChain chain)
			throws Throwable {
		if (channel instanceof HttpChannel) {
			return filter((HttpChannel) channel, containAnnotationParameterConfig, chain);
		}
		return chain.doFilter(channel, containAnnotationParameterConfig);
	}

	public abstract Object filter(HttpChannel httpChannel, ContainAnnotationParameterConfig containAnnotationParameterConfig,
			ParameterFilterChain chain) throws Throwable;
}
