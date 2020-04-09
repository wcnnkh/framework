package scw.mvc.page;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;

public final class ConfigurationPageFactory extends MultiPageFactory{
	private static final long serialVersionUID = 1L;

	public ConfigurationPageFactory(BeanFactory beanFactory){
		addAll(BeanUtils.getConfigurationList(PageFactoryAdapter.class, beanFactory));
	}
}
