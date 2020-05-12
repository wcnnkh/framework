package scw.mvc.service;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.core.instance.annotation.Configuration;
import scw.mvc.exception.ExceptionHandlerChain;
import scw.mvc.output.Output;
import scw.value.property.PropertyFactory;

@Configuration(order=Integer.MIN_VALUE)
public final class ConfigurationChannelService extends DefaultChannelService {

	public ConfigurationChannelService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		super(getHandlerChain(beanFactory, propertyFactory),
				getWarnExecuteTime(propertyFactory), beanFactory
						.getInstance(Output.class), beanFactory
						.getInstance(ExceptionHandlerChain.class));
	}

	private static FilterChain getHandlerChain(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		Collection<Filter> handlers = InstanceUtils.getConfigurationList(Filter.class, beanFactory,
				propertyFactory);
		FilterChain chain = new DefaultFilterChain(handlers, null);
		return chain;
	}

	private static long getWarnExecuteTime(PropertyFactory propertyFactory) {
		return propertyFactory.getValue("mvc.warn-execute-time", long.class,
				100L);
	}
}
