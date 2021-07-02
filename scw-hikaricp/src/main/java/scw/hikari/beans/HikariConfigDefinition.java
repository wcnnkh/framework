package scw.hikari.beans;

import com.zaxxer.hikari.HikariConfig;

import scw.beans.BeanUtils;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.db.Configurable;
import scw.hikari.HikariUtils;
import scw.instance.InstanceException;
import scw.logger.Levels;

public class HikariConfigDefinition extends DefaultBeanDefinition {

	public HikariConfigDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, HikariConfig.class);
	}

	@Override
	public boolean isInstance(Class<?>[] parameterTypes) {
		return beanFactory.isInstance(Configurable.class);
	}

	@Override
	public Object create() throws InstanceException {
		Configurable configurable = beanFactory.getInstance(Configurable.class);
		HikariConfig hikariConfig = new HikariConfig();
		HikariUtils.config(hikariConfig, configurable);
		BeanUtils.configurationProperties(hikariConfig, getEnvironment(), "db.hikari", Levels.INFO);
		return hikariConfig;
	}
}
