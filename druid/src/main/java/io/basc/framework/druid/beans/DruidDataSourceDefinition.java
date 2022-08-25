package io.basc.framework.druid.beans;

import com.alibaba.druid.pool.DruidDataSource;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.BeanConfigurator;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.db.Configurable;
import io.basc.framework.druid.DruidUtils;
import io.basc.framework.factory.InstanceException;

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
		BeanConfigurator beanConfigurator = new BeanConfigurator(getEnvironment());
		beanConfigurator.getContext().setNamePrefix("druid.");
		beanConfigurator.transform(dataSource);
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
