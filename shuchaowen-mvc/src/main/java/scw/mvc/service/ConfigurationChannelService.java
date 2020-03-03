package scw.mvc.service;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.mvc.handler.DefaultHandlerChain;
import scw.mvc.handler.Handler;
import scw.mvc.handler.HandlerChain;

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
		return new DefaultHandlerChain(
				getHandlers(beanFactory, propertyFactory), null);
	}

	private static long getWarnExecuteTime(PropertyFactory propertyFactory) {
		return StringUtils.parseLong(
				propertyFactory.getProperty("mvc.warn-execute-time"), 100);
	}
}
