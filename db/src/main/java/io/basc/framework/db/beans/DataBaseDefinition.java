package io.basc.framework.db.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.db.Configurable;
import io.basc.framework.db.DB;
import io.basc.framework.db.DataBase;
import io.basc.framework.db.DataBaseResolver;
import io.basc.framework.db.DefaultDB;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.sql.orm.SqlDialect;
import io.basc.framework.util.StringUtils;

public class DataBaseDefinition extends DefaultBeanDefinition {

	public DataBaseDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DB.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(ConnectionFactory.class)
				&& beanFactory.isInstance(Configurable.class);
	}

	@Override
	public Object create() throws InstanceException {
		ConnectionFactory connectionFactory = beanFactory
				.getInstance(ConnectionFactory.class);
		Configurable configurable = beanFactory.getInstance(Configurable.class);
		SqlDialect sqlDialect = null;
		if (StringUtils.isNotEmpty(configurable.getSqlDialect())) {
			sqlDialect = beanFactory.getInstance(configurable.getSqlDialect());
		}

		if (configurable.isAutoCreateDataBase()) {
			DataBase dataBase = null;
			if (beanFactory.isInstance(DataBase.class)) {
				dataBase = beanFactory.getInstance(DataBase.class);
			} else if (StringUtils.isNotEmpty(configurable.getUrl())) {
				if (beanFactory.isInstance(DataBaseResolver.class)) {
					dataBase = beanFactory.getInstance(DataBaseResolver.class)
							.resolve(configurable.getDriverClassName(),
									configurable.getUrl(),
									configurable.getUsername(),
									configurable.getPassword());
				}
			}

			if (dataBase != null) {
				dataBase.create();
			}
		}

		if (sqlDialect == null && beanFactory.isInstance(SqlDialect.class)) {
			sqlDialect = beanFactory.getInstance(SqlDialect.class);
		}

		if (sqlDialect == null) {
			throw new NotSupportedException(SqlDialect.class.getName());
		}

		DB db = new DefaultDB(connectionFactory, sqlDialect);
		if (StringUtils.isNotEmpty(configurable.getAutoCreateTables())) {
			db.createTables(configurable.getAutoCreateTables(),
					configurable.isRegisterManager());
		}
		return db;
	}
}
