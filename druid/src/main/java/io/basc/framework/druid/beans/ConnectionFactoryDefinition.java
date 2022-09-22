package io.basc.framework.druid.beans;

import com.alibaba.druid.pool.DruidDataSource;

import io.basc.framework.druid.DruidConnectionFactory;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.support.FactoryBeanDefinition;

public class ConnectionFactoryDefinition extends FactoryBeanDefinition {

	public ConnectionFactoryDefinition(BeanFactory beanFactory) {
		super(beanFactory, DruidConnectionFactory.class);
	}

	@Override
	public boolean isInstance(Class<?>[] parameterTypes) {
		return getBeanFactory().isInstance(DruidDataSource.class);
	}

	@Override
	public Object create() throws InstanceException {
		DruidDataSource dataSource = getBeanFactory().getInstance(DruidDataSource.class);
		return new DruidConnectionFactory(dataSource);
	}
}
