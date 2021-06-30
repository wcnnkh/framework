package scw.hikari.beans;

import com.zaxxer.hikari.HikariDataSource;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.hikari.HikariConnectionFactory;
import scw.instance.InstanceException;

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
