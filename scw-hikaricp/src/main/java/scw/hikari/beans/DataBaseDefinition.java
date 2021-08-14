package scw.hikari.beans;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.db.DataBase;
import scw.db.DataBaseResolver;
import scw.hikari.HikariUtils;
import scw.instance.InstanceException;

import com.zaxxer.hikari.HikariDataSource;

public class DataBaseDefinition extends DefaultBeanDefinition {

	public DataBaseDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DataBase.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(HikariDataSource.class) && beanFactory.isInstance(DataBaseResolver.class);
	}

	@Override
	public Object create() throws InstanceException {
		HikariDataSource dataSource = beanFactory.getInstance(HikariDataSource.class);
		return HikariUtils.resolveDataBase(dataSource, beanFactory.getInstance(DataBaseResolver.class));
	}
}
