package scw.ibatis.beans;

import org.apache.ibatis.session.Configuration;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.core.utils.StringUtils;
import scw.instance.InstanceException;
import scw.io.Resource;

public class ConfigurationDefinition extends DefaultBeanDefinition {

	public ConfigurationDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, Configuration.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(IbatisProperties.class);
	}

	@Override
	public Object create() throws InstanceException {
		IbatisProperties ibatisProperties = beanFactory.getInstance(IbatisProperties.class);
		Configuration configuration;
		if (StringUtils.isNotEmpty(ibatisProperties.getConfigLocation())) {
			Resource resource = beanFactory.getEnvironment().getResource(ibatisProperties.getConfigLocation());
			configuration = ConfigurationUtils.build(resource);
		} else {
			configuration = new Configuration();
		}
		ConfigurationUtils.configurationEnvironment(configuration, beanFactory);
		ConfigurationUtils.configuration(configuration, beanFactory);
		return configuration;
	}
}
