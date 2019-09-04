package scw.mvc;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.core.aop.Invoker;

public final class MethodAction implements Action<Channel> {
	private final Invoker invoker;
	private final ParameterDefinition[] parameterDefinitions;
	private final Collection<ParameterFilter> parameterFilters;

	public MethodAction(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> clz, Method method) {
		this.invoker = BeanUtils.getInvoker(beanFactory, clz, method);
		this.parameterFilters = MVCUtils.getParameterFilters(beanFactory, clz, method);
		this.parameterDefinitions = MVCUtils.getParameterDefinitions(method);
	}

	public MethodAction(Invoker invoker, Collection<ParameterFilter> parameterFilters, Method method) {
		this.invoker = invoker;
		this.parameterFilters = parameterFilters;
		this.parameterDefinitions = MVCUtils.getParameterDefinitions(method);
	}

	public Object doAction(Channel channel) throws Throwable {
		Object[] args = MVCUtils.getParameterValues(channel, parameterDefinitions, parameterFilters);
		return invoker.invoke(args);
	}

	@Override
	public String toString() {
		return invoker.toString();
	}
}
