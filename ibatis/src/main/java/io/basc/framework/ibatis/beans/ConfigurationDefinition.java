package io.basc.framework.ibatis.beans;

import org.apache.ibatis.session.Configuration;

import io.basc.framework.beans.factory.InstanceException;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.support.ContextBeanDefinition;
import io.basc.framework.io.Resource;
import io.basc.framework.util.StringUtils;

public class ConfigurationDefinition extends ContextBeanDefinition {

	public ConfigurationDefinition(ApplicationContext context) {
		super(context, Configuration.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(IbatisProperties.class);
	}

	@Override
	public Object create() throws InstanceException {
		IbatisProperties ibatisProperties = getBeanFactory().getInstance(IbatisProperties.class);
		Configuration configuration;
		if (StringUtils.isNotEmpty(ibatisProperties.getConfigLocation())) {
			Resource resource = getEnvironment().getResourceLoader().getResource(ibatisProperties.getConfigLocation());
			configuration = ConfigurationUtils.build(resource);
		} else {
			configuration = new Configuration();
		}
		ConfigurationUtils.configurationEnvironment(configuration, getBeanFactory());
		ConfigurationUtils.configuration(configuration, getContext());
		return configuration;
	}
}
