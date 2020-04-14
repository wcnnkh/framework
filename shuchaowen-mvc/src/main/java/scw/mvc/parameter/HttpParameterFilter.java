package scw.mvc.parameter;

import scw.core.parameter.ParameterDescriptor;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.http.HttpChannel;

public abstract class HttpParameterFilter implements ParameterFilter {

	public final Object filter(Action action, Channel channel, ParameterDescriptor parameterConfig, ParameterFilterChain chain)
			throws Throwable {
		if (action instanceof HttpAction && channel instanceof HttpChannel) {
			return filter((HttpAction) action, (HttpChannel) channel, parameterConfig, chain);
		}
		return chain.doFilter(channel, parameterConfig);
	}

	protected abstract Object filter(HttpAction httpAction, HttpChannel channel, ParameterDescriptor parameterConfig, ParameterFilterChain chain)
			throws Throwable;
}
