package scw.druid.beans;

import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;

import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.db.ConnectionPoolConfig;
import scw.db.DBUtils;
import scw.druid.DruidConnectionFactory;
import scw.instance.InstanceException;
import scw.value.support.PropertiesPropertyFactory;

public class DruidDataSourceDefinition extends DefaultBeanDefinition {

	public DruidDataSourceDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DruidDataSource.class);
	}

	@Override
	public boolean isInstance(Class<?>[] parameterTypes) {
		return beanFactory.getEnvironment().exists(DBUtils.DEFAULT_CONFIGURATION)
				|| beanFactory.isInstance(ConnectionPoolConfig.class);
	}

	@Override
	public Object create() throws InstanceException {
		if (beanFactory.getEnvironment().exists(DBUtils.DEFAULT_CONFIGURATION)) {
			DruidDataSource dataSource = new DruidDataSource();
			scw.event.Observable<Properties> properties = getEnvironment().getProperties(DBUtils.DEFAULT_CONFIGURATION);
			DBUtils.loadProperties(dataSource, new PropertiesPropertyFactory(properties.get()));
			return dataSource;
		} else {
			ConnectionPoolConfig config = beanFactory.getInstance(ConnectionPoolConfig.class);
			DruidConnectionFactory connectionFactory = new DruidConnectionFactory(config);
			return connectionFactory.getDruidDataSource();
		}
	}

	@Override
	public void destroy(Object instance) throws BeansException {
		if (instance instanceof DruidDataSource) {
			((DruidDataSource) instance).close();
		}
		super.destroy(instance);
	}
}
