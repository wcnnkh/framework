package scw.mvc;

import java.util.Collection;

import scw.core.aop.Invoker;
import scw.core.parameter.ParameterConfig;

public abstract class AbstractAction implements Action, FilterChain {
	public abstract Invoker getInvoker();

	public abstract Collection<Filter> getFilters();

	public abstract Collection<ParameterFilter> getParameterFilters();

	public Object[] getArgs(ParameterConfig[] parameterConfigs, Channel channel) {
		return MVCUtils.getParameterValues(channel, parameterConfigs, getParameterFilters());
	}

	public Object doFilter(Channel channel) throws Throwable {
		return getInvoker().invoke(getArgs(getParameterConfigs(), channel));
	}

	public final Object doAction(Channel channel) throws Throwable {
		FilterChain chain = new SimpleFilterChain(getFilters(), this);
		return chain.doFilter(channel);
	}

	@Override
	public final String toString() {
		return getInvoker().toString();
	}
}
