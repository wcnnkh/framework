package scw.druid.beans;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.db.DataBase;
import scw.db.DataBaseResolver;
import scw.druid.DruidUtils;
import scw.instance.InstanceException;

import com.alibaba.druid.pool.DruidDataSource;

public class DataBaseDefinition extends DefaultBeanDefinition {

	public DataBaseDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DataBase.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(DruidDataSource.class)
				&& beanFactory.isInstance(DataBaseResolver.class);
	}

	@Override
	public Object create() throws InstanceException {
		DruidDataSource druidDataSource = beanFactory
				.getInstance(DruidDataSource.class);
		return DruidUtils.resolve(druidDataSource,
				beanFactory.getInstance(DataBaseResolver.class));
	}
}
