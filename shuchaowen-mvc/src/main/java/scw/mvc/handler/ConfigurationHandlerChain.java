package scw.mvc.handler;

import java.util.LinkedList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;

public final class ConfigurationHandlerChain extends DefaultHandlerChain {

	public ConfigurationHandlerChain(BeanFactory beanFactory) {
		super(getHandlers(beanFactory), null);
	}

	private static List<Handler> getHandlers(BeanFactory beanFactory) {
		List<Handler> handlers = new LinkedList<Handler>();
		handlers.addAll(BeanUtils.getConfigurationList(Handler.class,
				beanFactory));
		return handlers;
	}
}
