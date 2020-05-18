package scw.freemarker;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.value.property.PropertyFactory;
import freemarker.cache.TemplateLoader;

public class DefaultTemplateLoader extends MultiTemplateLoader {
	private static final long serialVersionUID = 1L;

	public DefaultTemplateLoader(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		addAll(InstanceUtils.getConfigurationList(TemplateLoader.class,
				beanFactory, propertyFactory));
	}
}
