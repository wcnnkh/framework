package io.basc.framework.db.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.db.Configurable;
import io.basc.framework.db.DBUtils;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.value.support.PropertiesPropertyFactory;

import java.util.Properties;

public class ConfigurableDefinition extends DefaultBeanDefinition{

	public ConfigurableDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, Configurable.class);
	}
	
	@Override
	public boolean isInstance() {
		return beanFactory.getEnvironment().exists(DBUtils.DEFAULT_CONFIGURATION);
	}
	
	@Override
	public Object create() throws InstanceException {
		Configurable configurable = new Configurable();
		io.basc.framework.event.Observable<Properties> properties = getEnvironment().getProperties(DBUtils.DEFAULT_CONFIGURATION);
		DBUtils.loadProperties(configurable, new PropertiesPropertyFactory(properties.get()));
		return configurable;
	}
}
