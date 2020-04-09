package scw.mvc.action.filter;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;

public final class ConfigurationActionFilterChain extends DefaultActionFilterChain{

	public ConfigurationActionFilterChain(BeanFactory beanFactory) {
		super(InstanceUtils.getConfigurationList(ActionFilter.class, beanFactory), null);
	}

}
