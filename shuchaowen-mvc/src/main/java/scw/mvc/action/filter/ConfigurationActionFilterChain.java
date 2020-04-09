package scw.mvc.action.filter;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;

public final class ConfigurationActionFilterChain extends DefaultActionFilterChain{

	public ConfigurationActionFilterChain(BeanFactory beanFactory) {
		super(BeanUtils.getConfigurationList(ActionFilter.class, beanFactory), null);
	}

}
