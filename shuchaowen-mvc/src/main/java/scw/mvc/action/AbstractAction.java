package scw.mvc.action;

import java.util.Collection;

import scw.aop.Invoker;
import scw.core.parameter.ParameterConfig;
import scw.core.utils.CollectionUtils;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.action.filter.IteratorActionFilterChain;
import scw.mvc.parameter.ParameterFilter;

public abstract class AbstractAction implements Action{
	public abstract Invoker getInvoker();
	
	public abstract Collection<ActionFilter> getActionFilters();

	public abstract Collection<ParameterFilter> getParameterFilters();

	public abstract ParameterConfig[] getParameterConfigs();

	public Object[] getArgs(ParameterConfig[] parameterConfigs, Channel channel) {
		return MVCUtils.getParameterValues(channel, parameterConfigs,
				getParameterFilters(), null);
	}
	
	public ActionFilterChain getActionFilterChain() {
		Collection<ActionFilter> filters = getActionFilters();
		return CollectionUtils.isEmpty(filters)? null:new IteratorActionFilterChain(filters, null);
	}
	
	public final Object doAction(Channel channel) throws Throwable {
		return getInvoker().invoke(getArgs(getParameterConfigs(), channel));
	}
	
	@Override
	public final String toString() {
		return getInvoker().toString();
	}
}
