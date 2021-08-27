package io.basc.framework.druid.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.druid.DruidConnectionFactory;
import io.basc.framework.instance.InstanceException;

import com.alibaba.druid.pool.DruidDataSource;

public class ConnectionFactoryDefinition extends DefaultBeanDefinition {

	public ConnectionFactoryDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DruidConnectionFactory.class);
	}

	@Override
	public boolean isInstance(Class<?>[] parameterTypes) {
		return beanFactory.isInstance(DruidDataSource.class);
	}

	@Override
	public Object create() throws InstanceException {
		DruidDataSource dataSource = beanFactory.getInstance(DruidDataSource.class);
		return new DruidConnectionFactory(dataSource);
	}
}
