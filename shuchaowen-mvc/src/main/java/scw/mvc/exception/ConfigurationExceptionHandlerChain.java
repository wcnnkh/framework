package scw.mvc.exception;

import java.util.LinkedList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.util.value.property.PropertyFactory;

public final class ConfigurationExceptionHandlerChain extends
		DefaultExceptionHandlerChain {

	public ConfigurationExceptionHandlerChain(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		super(getActionExceptionHandlers(beanFactory, propertyFactory), null);
	}

	private static List<ExceptionHandler> getActionExceptionHandlers(
			BeanFactory beanFactory, PropertyFactory propertyFactory) {
		List<ExceptionHandler> handlers = new LinkedList<ExceptionHandler>();
		handlers.addAll(InstanceUtils.getConfigurationList(
				ExceptionHandler.class, beanFactory, propertyFactory));

		if (beanFactory.isInstance(DefaultExceptionHandler.class)) {
			handlers.add(beanFactory.getInstance(DefaultExceptionHandler.class));
		}
		return handlers;
	}
}
