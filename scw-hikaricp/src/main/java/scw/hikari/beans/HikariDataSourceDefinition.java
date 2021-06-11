package scw.hikari.beans;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.instance.InstanceException;

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
			((HikariDataSource) instance).close();
		}
		super.destroy(instance);
	}
}
