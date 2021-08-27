package io.basc.framework.hikari.beans;

import io.basc.framework.beans.BeanUtils;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.db.Configurable;
import io.basc.framework.hikari.HikariUtils;
import io.basc.framework.instance.InstanceException;
import io.basc.framework.logger.Levels;

import com.zaxxer.hikari.HikariConfig;

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
