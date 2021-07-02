package scw.db.beans;

import java.util.Properties;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.db.Configurable;
import scw.db.DBUtils;
import scw.instance.InstanceException;
import scw.value.support.PropertiesPropertyFactory;

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
		scw.event.Observable<Properties> properties = getEnvironment().getProperties(DBUtils.DEFAULT_CONFIGURATION);
		DBUtils.loadProperties(configurable, new PropertiesPropertyFactory(properties.get()));
		return configurable;
	}
}
