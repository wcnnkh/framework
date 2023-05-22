package io.basc.framework.hikari.beans;

import com.zaxxer.hikari.HikariDataSource;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.hikari.HikariConnectionFactory;

public class ConnectionFactoryDefinition extends FactoryBeanDefinition {

	public ConnectionFactoryDefinition(BeanFactory beanFactory) {
		super(beanFactory, HikariConnectionFactory.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(HikariDataSource.class);
	}

	@Override
	public Object create() throws InstanceException {
		HikariDataSource dataSource = getBeanFactory().getInstance(HikariDataSource.class);
		return new HikariConnectionFactory(dataSource);
	}
}
