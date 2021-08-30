package io.basc.framework.hikari.beans;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.factory.InstanceException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariDataSourceDefinition extends DefaultBeanDefinition {

	public HikariDataSourceDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, HikariDataSource.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(HikariConfig.class);
	}

	@Override
	public Object create() throws InstanceException {
		HikariConfig config = beanFactory.getInstance(HikariConfig.class);
		return new HikariDataSource(config);
	}

	@Override
	public void destroy(Object instance) throws BeansException {
		if (instance instanceof HikariDataSource) {
			HikariDataSource dataSource = (HikariDataSource) instance;
			if (!dataSource.isClosed()) {
				dataSource.close();
			}
		}
		super.destroy(instance);
	}
}
