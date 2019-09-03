package scw.mvc;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.core.aop.Invoker;

public final class MethodAction<T extends Channel> implements Action<T> {
	private final Invoker invoker;
	private final ParameterDefinition[] parameterDefinitions;
	private final Collection<ParameterFilter> parameterFilters;

	public MethodAction(Invoker invoker, Collection<ParameterFilter> parameterFilters, Method method) {
		this.invoker = invoker;
		this.parameterFilters = parameterFilters;
		this.parameterDefinitions = MVCUtils.getParameterDefinitions(method);
	}

	public void doAction(T channel) throws Throwable {
		Object[] args = MVCUtils.getParameterValues(channel, parameterDefinitions, parameterFilters);
		Object rtn = invoker.invoke(args);
		channel.write(rtn);
	}
}
