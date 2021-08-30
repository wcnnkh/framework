package io.basc.framework.ibatis.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.instance.InstanceException;
import io.basc.framework.io.Resource;
import io.basc.framework.util.StringUtils;

import org.apache.ibatis.session.Configuration;

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
