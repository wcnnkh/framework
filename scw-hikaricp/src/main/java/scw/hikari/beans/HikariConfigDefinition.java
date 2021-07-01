package scw.hikari.beans;

import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.db.Configurable;
import scw.db.DBUtils;
import scw.event.Observable;
import scw.hikari.HikariUtils;
import scw.instance.InstanceException;

public class HikariConfigDefinition extends DefaultBeanDefinition {

	public HikariConfigDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, HikariConfig.class);
	}

	@Override
	public boolean isInstance(Class<?>[] parameterTypes) {
		return beanFactory.getEnvironment().exists(DBUtils.DEFAULT_CONFIGURATION)
				|| beanFactory.isInstance(Configurable.class);
	}

	@Override
	public Object create() throws InstanceException {
		if (beanFactory.getEnvironment().exists(DBUtils.DEFAULT_CONFIGURATION)) {
			Observable<Properties> properties = beanFactory.getEnvironment()
					.getProperties(DBUtils.DEFAULT_CONFIGURATION);
			HikariConfig config = new HikariConfig(properties.get());
			return config;
		} else {
			Configurable properties = beanFactory.getInstance(Configurable.class);
			HikariConfig config = new HikariConfig();
			HikariUtils.config(config, properties);
			return config;
		}
	}
}
