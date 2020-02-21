package scw.mvc.support.action;

import java.util.Collection;

import scw.aop.Invoker;
import scw.core.parameter.ParameterConfig;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;
import scw.mvc.ParameterFilter;
import scw.mvc.SimpleFilterChain;

public abstract class AbstractAction implements Action, FilterChain {
	public abstract Invoker getInvoker();

	public abstract Collection<Filter> getFilters();

	public abstract Collection<ParameterFilter> getParameterFilters();
	
	public abstract ParameterConfig[] getParameterConfigs();

	public Object[] getArgs(ParameterConfig[] parameterConfigs, Channel channel) {
		return MVCUtils.getParameterValues(channel, parameterConfigs, getParameterFilters(), null);
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
