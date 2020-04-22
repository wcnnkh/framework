package scw.mvc.action.notfound;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.core.instance.InstanceUtils;
import scw.core.instance.annotation.Configuration;
import scw.mvc.action.notfound.adapter.NotFoundAdapter;
import scw.util.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE, value=NotFoundService.class)
@Bean(proxy = false)
public class ConfigurationNotfoundService extends DefaultNotfoundService {
	private static final long serialVersionUID = 1L;

	public ConfigurationNotfoundService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		addAll(InstanceUtils.getConfigurationList(NotFoundAdapter.class,
				beanFactory, propertyFactory));
	}
}
