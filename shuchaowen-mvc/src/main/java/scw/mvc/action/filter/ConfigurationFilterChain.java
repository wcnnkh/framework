package scw.mvc.action.filter;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;

public final class ConfigurationFilterChain extends DefaultFilterChain{

	public ConfigurationFilterChain(BeanFactory beanFactory) {
		super(BeanUtils.getConfigurationList(Filter.class, beanFactory), null);
	}

}
