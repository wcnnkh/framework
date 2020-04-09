package scw.mvc.service;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.mvc.exception.ExceptionHandlerChain;
import scw.mvc.handler.DefaultHandlerChain;
import scw.mvc.handler.Handler;
import scw.mvc.handler.HandlerChain;
import scw.mvc.output.Output;
import scw.util.value.property.PropertyFactory;

public final class ConfigurationChannelService extends DefaultChannelService {

	public ConfigurationChannelService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		super(getHandlerChain(beanFactory),
				getWarnExecuteTime(propertyFactory), beanFactory
						.getInstance(Output.class), beanFactory
						.getInstance(ExceptionHandlerChain.class));
	}

	private static Collection<Handler> getHandlers(BeanFactory beanFactory) {
		return InstanceUtils.getConfigurationList(Handler.class, beanFactory);
	}

	private static HandlerChain getHandlerChain(BeanFactory beanFactory) {
		HandlerChain chain = new DefaultHandlerChain(getHandlers(beanFactory),
				null);
		return chain;
	}

	private static long getWarnExecuteTime(PropertyFactory propertyFactory) {
		return propertyFactory.getValue("mvc.warn-execute-time", long.class, 100L);
	}
}
