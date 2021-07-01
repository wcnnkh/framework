package scw.druid.beans;

import com.alibaba.druid.pool.DruidDataSource;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.db.DBUtils;
import scw.db.database.DataBase;
import scw.instance.InstanceException;

public class DataBaseDefinition extends DefaultBeanDefinition {

	public DataBaseDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DataBase.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(DruidDataSource.class);
	}

	@Override
	public Object create() throws InstanceException {
		DruidDataSource druidDataSource = beanFactory.getInstance(DruidDataSource.class);
		return DBUtils.automaticRecognition(druidDataSource.getDriverClassName(), druidDataSource.getUrl(),
				druidDataSource.getUsername(), druidDataSource.getPassword());
	}
}
