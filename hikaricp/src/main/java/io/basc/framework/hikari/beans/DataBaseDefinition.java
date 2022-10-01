package io.basc.framework.hikari.beans;

import com.zaxxer.hikari.HikariDataSource;

import io.basc.framework.db.DataBase;
import io.basc.framework.db.DataBaseResolver;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.hikari.HikariUtils;

public class DataBaseDefinition extends FactoryBeanDefinition {

	public DataBaseDefinition(BeanFactory beanFactory) {
		super(beanFactory, DataBase.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(HikariDataSource.class)
				&& getBeanFactory().isInstance(DataBaseResolver.class);
	}

	@Override
	public Object create() throws InstanceException {
		HikariDataSource dataSource = getBeanFactory().getInstance(HikariDataSource.class);
		return HikariUtils.resolveDataBase(dataSource, getBeanFactory().getInstance(DataBaseResolver.class));
	}
}
