package scw.mvc.action;

import java.util.Collection;

import scw.aop.Invoker;
import scw.core.parameter.ParameterConfig;
import scw.core.utils.CollectionUtils;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.action.filter.Filter;
import scw.mvc.action.filter.FilterChain;
import scw.mvc.action.filter.IteratorFilterChain;
import scw.mvc.parameter.ParameterFilter;

public abstract class AbstractAction implements Action{
	public abstract Invoker getInvoker();
	
	public abstract Collection<Filter> getFilters();

	public abstract Collection<ParameterFilter> getParameterFilters();

	public abstract ParameterConfig[] getParameterConfigs();

	public Object[] getArgs(ParameterConfig[] parameterConfigs, Channel channel) {
		return MVCUtils.getParameterValues(channel, parameterConfigs,
				getParameterFilters(), null);
	}
	
	public FilterChain getFilterChain() {
		Collection<Filter> filters = getFilters();
		return CollectionUtils.isEmpty(filters)? null:new IteratorFilterChain(getFilters(), null);
	}
	
	public final Object doAction(Channel channel) throws Throwable {
		return getInvoker().invoke(getArgs(getParameterConfigs(), channel));
	}
	
	@Override
	public final String toString() {
		return getInvoker().toString();
	}
}
