package io.basc.framework.hikari.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.hikari.HikariConnectionFactory;
import io.basc.framework.instance.InstanceException;

import com.zaxxer.hikari.HikariDataSource;

public class ConnectionFactoryDefinition extends DefaultBeanDefinition {

	public ConnectionFactoryDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, HikariConnectionFactory.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(HikariDataSource.class);
	}

	@Override
	public Object create() throws InstanceException {
		HikariDataSource dataSource = beanFactory.getInstance(HikariDataSource.class);
		return new HikariConnectionFactory(dataSource);
	}
}
