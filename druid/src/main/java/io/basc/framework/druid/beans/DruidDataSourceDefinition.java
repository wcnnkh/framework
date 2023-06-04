package io.basc.framework.druid.beans;

import com.alibaba.druid.pool.DruidDataSource;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.InstanceException;
import io.basc.framework.context.Context;
import io.basc.framework.context.support.ContextBeanDefinition;
import io.basc.framework.context.support.ContextConfigurator;
import io.basc.framework.db.Configurable;
import io.basc.framework.druid.DruidUtils;

public class DruidDataSourceDefinition extends ContextBeanDefinition {

	public DruidDataSourceDefinition(Context context) {
		super(context, DruidDataSource.class);
	}

	@Override
	public boolean isInstance(Class<?>[] parameterTypes) {
		return getBeanFactory().isInstance(Configurable.class);
	}

	@Override
	public Object create() throws InstanceException {
		Configurable configurable = getContext().getInstance(Configurable.class);
		DruidDataSource dataSource = new DruidDataSource();
		DruidUtils.config(dataSource, configurable);
		ContextConfigurator contextConfigurator = new ContextConfigurator(getContext());
		contextConfigurator.getContext().setNamePrefix("druid.");
		contextConfigurator.transform(dataSource);
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
