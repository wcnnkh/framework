package io.basc.framework.druid.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.db.DataBase;
import io.basc.framework.db.DataBaseResolver;
import io.basc.framework.druid.DruidUtils;
import io.basc.framework.factory.InstanceException;

import com.alibaba.druid.pool.DruidDataSource;

public class DataBaseDefinition extends DefaultBeanDefinition {

	public DataBaseDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DataBase.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(DruidDataSource.class) && beanFactory.isInstance(DataBaseResolver.class);
	}

	@Override
	public Object create() throws InstanceException {
		DruidDataSource druidDataSource = beanFactory.getInstance(DruidDataSource.class);
		return DruidUtils.resolve(druidDataSource, beanFactory.getInstance(DataBaseResolver.class));
	}
}
