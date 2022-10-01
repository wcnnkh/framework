package io.basc.framework.hikari.beans;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.support.FactoryBeanDefinition;

public class HikariDataSourceDefinition extends FactoryBeanDefinition {

	public HikariDataSourceDefinition(BeanFactory beanFactory) {
		super(beanFactory, HikariDataSource.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(HikariConfig.class);
	}

	@Override
	public Object create() throws InstanceException {
		HikariConfig config = getBeanFactory().getInstance(HikariConfig.class);
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
