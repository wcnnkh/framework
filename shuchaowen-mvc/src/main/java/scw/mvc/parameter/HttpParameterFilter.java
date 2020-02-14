package scw.mvc.parameter;

import scw.core.parameter.ParameterConfig;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.ParameterFilter;
import scw.mvc.ParameterFilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.support.action.HttpAction;

public abstract class HttpParameterFilter implements ParameterFilter {

	public Object filter(Action action, Channel channel, ParameterConfig parameterConfig, ParameterFilterChain chain)
			throws Throwable {
		if (action instanceof HttpAction && channel instanceof HttpChannel) {
			return filter((HttpAction) action, (HttpChannel) channel, parameterConfig, chain);
		}
		return chain.doFilter(channel, parameterConfig);
	}

	protected abstract Object filter(HttpChannel channel, ParameterConfig parameterConfig, ParameterFilterChain chain)
			throws Throwable;
}
