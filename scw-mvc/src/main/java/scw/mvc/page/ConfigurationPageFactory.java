package scw.mvc.page;

import scw.beans.BeanFactory;

public final class ConfigurationPageFactory extends MultiPageFactory{
	private static final long serialVersionUID = 1L;

	public ConfigurationPageFactory(BeanFactory beanFactory){
		for(PageFactory pageFactory : beanFactory.getServiceLoader(PageFactory.class)){
			add(pageFactory);
		}
	}
}
