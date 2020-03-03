package scw.mvc.action;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.annotation.Configuration;
import scw.core.PropertyFactory;
import scw.mvc.action.exception.ActionExceptionHandlerChain;
import scw.mvc.action.filter.Filter;
import scw.mvc.action.output.ActionOutput;

@Configuration(order = ConfigurationActionHandler.ORDER)
public final class ConfigurationActionHandler extends ActionHandler {
	public static final int ORDER = -1000;
	
	public ConfigurationActionHandler(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		super(beanFactory.getInstance(ActionFactory.class), 
				BeanUtils.getConfigurationList(Filter.class, beanFactory, propertyFactory), beanFactory.getInstance(ActionOutput.class), beanFactory.getInstance(ActionExceptionHandlerChain.class));
	}
}
