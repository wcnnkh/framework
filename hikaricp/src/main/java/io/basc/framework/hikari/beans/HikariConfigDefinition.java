package io.basc.framework.hikari.beans;

import com.zaxxer.hikari.HikariConfig;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.beans.support.BeanConfigurator;
import io.basc.framework.db.Configurable;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.hikari.HikariUtils;

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
		BeanConfigurator configurator = new BeanConfigurator(getEnvironment());
		configurator.getContext().setNamePrefix("hikari.");
		configurator.transform(hikariConfig);
		return hikariConfig;
	}
}
