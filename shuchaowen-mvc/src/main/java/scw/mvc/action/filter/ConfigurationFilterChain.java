package scw.mvc.action.filter;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;

public final class ConfigurationFilterChain extends DefaultFilterChain{

	public ConfigurationFilterChain(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		super(BeanUtils.getConfigurationList(Filter.class, beanFactory, propertyFactory), null);
	}

}
