package scw.mvc.handler;

import java.util.LinkedList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;

public final class ConfigurationHandlerChain extends DefaultHandlerChain {

	public ConfigurationHandlerChain(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		super(getHandlers(beanFactory, propertyFactory), null);
	}

	private static List<Handler> getHandlers(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		List<Handler> handlers = new LinkedList<Handler>();
		handlers.addAll(BeanUtils.getConfigurationList(Handler.class,
				beanFactory, propertyFactory));
		return handlers;
	}
}
