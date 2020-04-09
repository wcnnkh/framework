package scw.mvc.exception;

import java.util.LinkedList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.util.value.property.PropertyFactory;


public final class ConfigurationExceptionHandlerChain extends DefaultExceptionHandlerChain{

	public ConfigurationExceptionHandlerChain(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		super(getActionExceptionHandlers(beanFactory), null);
	}

	private static List<ExceptionHandler> getActionExceptionHandlers(BeanFactory beanFactory){
		List<ExceptionHandler> handlers = new LinkedList<ExceptionHandler>();
		handlers.addAll(BeanUtils.getConfigurationList(ExceptionHandler.class, beanFactory));
		
		if (beanFactory.isInstance(DefaultExceptionHandler.class)) {
			handlers.add(beanFactory
					.getInstance(DefaultExceptionHandler.class));
		}
		return handlers;
	}
}
