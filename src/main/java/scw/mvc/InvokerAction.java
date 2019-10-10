package scw.mvc;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.core.PropertyFactory;
import scw.core.aop.Invoker;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ParameterConfig;
import scw.core.reflect.ParameterUtils;

public class InvokerAction implements Action<Channel> {
	private final Invoker invoker;
	private final ParameterConfig[] parameterConfigs;
	private final Collection<ParameterFilter> parameterFilters;

	public InvokerAction(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clz,
			Method method) {
		this.invoker = InstanceUtils.getInvoker(instanceFactory, clz, method);
		this.parameterFilters = MVCUtils.getParameterFilters(instanceFactory, clz, method);
		this.parameterConfigs = ParameterUtils.getParameterConfigs(method);
	}

	public Object doAction(Channel channel) throws Throwable {
		Object[] args = MVCUtils.getParameterValues(channel, parameterConfigs, parameterFilters);
		return invoker.invoke(args);
	}

	@Override
	public String toString() {
		return invoker.toString();
	}
}
