package scw.druid.beans;

import com.alibaba.druid.pool.DruidDataSource;

import scw.beans.BeanUtils;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.db.Configurable;
import scw.druid.DruidUtils;
import scw.instance.InstanceException;
import scw.logger.Levels;

public class DruidDataSourceDefinition extends DefaultBeanDefinition {

	public DruidDataSourceDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DruidDataSource.class);
	}

	@Override
	public boolean isInstance(Class<?>[] parameterTypes) {
		return beanFactory.isInstance(Configurable.class);
	}

	@Override
	public Object create() throws InstanceException {
		Configurable configurable = beanFactory.getInstance(Configurable.class);
		DruidDataSource dataSource = new DruidDataSource();
		DruidUtils.config(dataSource, configurable);
		BeanUtils.configurationProperties(dataSource, getEnvironment(), "db.druid", Levels.INFO);
		return dataSource;
	}

	@Override
	public void destroy(Object instance) throws BeansException {
		super.destroy(instance);
		if (instance instanceof DruidDataSource) {
			DruidDataSource dataSource = (DruidDataSource) instance;
			if (!dataSource.isClosed()) {
				dataSource.close();
			}
		}
	}
}
