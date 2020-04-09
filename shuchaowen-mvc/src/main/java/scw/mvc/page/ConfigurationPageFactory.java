package scw.mvc.page;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;

public final class ConfigurationPageFactory extends MultiPageFactory{
	private static final long serialVersionUID = 1L;

	public ConfigurationPageFactory(BeanFactory beanFactory){
		addAll(InstanceUtils.getConfigurationList(PageFactoryAdapter.class, beanFactory));
	}
}
