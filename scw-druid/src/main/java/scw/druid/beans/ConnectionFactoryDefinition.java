package scw.druid.beans;

import com.alibaba.druid.pool.DruidDataSource;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.druid.DruidConnectionFactory;
import scw.instance.InstanceException;

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
