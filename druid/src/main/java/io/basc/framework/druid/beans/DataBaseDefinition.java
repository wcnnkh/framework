package io.basc.framework.druid.beans;

import com.alibaba.druid.pool.DruidDataSource;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.InstanceException;
import io.basc.framework.beans.factory.support.FactoryBeanDefinition;
import io.basc.framework.db.Database;
import io.basc.framework.db.DataBaseResolver;
import io.basc.framework.druid.DruidUtils;

public class DataBaseDefinition extends FactoryBeanDefinition {

	public DataBaseDefinition(BeanFactory beanFactory) {
		super(beanFactory, Database.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(DruidDataSource.class) && getBeanFactory().isInstance(DataBaseResolver.class);
	}

	@Override
	public Object create() throws InstanceException {
		DruidDataSource druidDataSource = getBeanFactory().getInstance(DruidDataSource.class);
		return DruidUtils.resolve(druidDataSource, getBeanFactory().getInstance(DataBaseResolver.class));
	}
}
