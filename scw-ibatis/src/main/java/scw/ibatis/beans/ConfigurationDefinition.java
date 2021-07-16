package scw.ibatis.beans;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.session.Configuration;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.instance.InstanceException;
import scw.io.Resource;
import scw.io.ResourceUtils;

public class ConfigurationDefinition extends DefaultBeanDefinition {

	public ConfigurationDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, Configuration.class);
	}

	@Override
	public boolean isInstance() {
		return true;
	}

	@Override
	public Object create() throws InstanceException {
		Resource resource = beanFactory.getEnvironment().getResource("mybatis-config.xml");
		Configuration configuration;
		if (resource != null && resource.exists()) {
			XMLConfigBuilder builder = new XMLConfigBuilder(ResourceUtils.getInputStream(resource));
			builder.parse();
			configuration = builder.getConfiguration();
		} else {
			configuration = new Configuration();
		}
		ConfigurationUtils.configurationEnvironment(configuration, beanFactory);
		ConfigurationUtils.configuration(configuration, beanFactory);
		return configuration;
	}
}
