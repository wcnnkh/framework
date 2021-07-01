package scw.hikari.beans;

import com.zaxxer.hikari.HikariDataSource;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.db.database.DataBase;
import scw.hikari.HikariUtils;
import scw.instance.InstanceException;

public class DataBaseDefinition extends DefaultBeanDefinition {

	public DataBaseDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DataBase.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(HikariDataSource.class);
	}

	@Override
	public Object create() throws InstanceException {
		HikariDataSource dataSource = beanFactory.getInstance(HikariDataSource.class);
		return HikariUtils.resolveDataBase(dataSource);
	}
}
