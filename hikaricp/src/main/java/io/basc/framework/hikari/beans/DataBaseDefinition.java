package io.basc.framework.hikari.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.db.DataBase;
import io.basc.framework.db.DataBaseResolver;
import io.basc.framework.hikari.HikariUtils;
import io.basc.framework.instance.InstanceException;

import com.zaxxer.hikari.HikariDataSource;

public class DataBaseDefinition extends DefaultBeanDefinition {

	public DataBaseDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DataBase.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(HikariDataSource.class) && beanFactory.isInstance(DataBaseResolver.class);
	}

	@Override
	public Object create() throws InstanceException {
		HikariDataSource dataSource = beanFactory.getInstance(HikariDataSource.class);
		return HikariUtils.resolveDataBase(dataSource, beanFactory.getInstance(DataBaseResolver.class));
	}
}
