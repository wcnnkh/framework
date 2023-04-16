package io.basc.framework.db.beans;

import java.util.Properties;

import io.basc.framework.db.Configurable;
import io.basc.framework.db.DBUtils;
import io.basc.framework.env.Environment;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.value.PropertiesPropertyFactory;

public class ConfigurableDefinition extends FactoryBeanDefinition {
	private final Environment environment;

	public ConfigurableDefinition(Environment environment) {
		super(environment, Configurable.class);
		this.environment = environment;
	}

	@Override
	public boolean isInstance() {
		return environment.getResourceLoader().exists(DBUtils.DEFAULT_CONFIGURATION);
	}

	@Override
	public Object create() throws InstanceException {
		Configurable configurable = new Configurable();
		Observable<Properties> properties = environment.getProperties(DBUtils.DEFAULT_CONFIGURATION);
		DBUtils.loadProperties(configurable, new PropertiesPropertyFactory(properties.get()));
		return configurable;
	}
}
