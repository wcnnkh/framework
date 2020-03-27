package scw.mvc.service;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.utils.StringUtils;
import scw.mvc.handler.DefaultHandlerChain;
import scw.mvc.handler.Handler;
import scw.mvc.handler.HandlerChain;
import scw.util.value.property.PropertyFactory;

public final class ConfigurationChannelService extends
		DefaultChannelService {

	public ConfigurationChannelService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		super(getHandlerChain(beanFactory, propertyFactory),
				getWarnExecuteTime(propertyFactory));
	}

	private static Collection<Handler> getHandlers(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		return BeanUtils.getConfigurationList(Handler.class, beanFactory,
				propertyFactory);
	}

	private static HandlerChain getHandlerChain(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		HandlerChain chain =  new DefaultHandlerChain(
				getHandlers(beanFactory, propertyFactory), null);
		return chain;
	}

	private static long getWarnExecuteTime(PropertyFactory propertyFactory) {
		return StringUtils.parseLong(
				propertyFactory.getString("mvc.warn-execute-time"), 100);
	}
}
