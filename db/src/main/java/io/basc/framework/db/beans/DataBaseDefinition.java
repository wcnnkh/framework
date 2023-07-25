package io.basc.framework.db.beans;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.InstanceException;
import io.basc.framework.beans.factory.support.FactoryBeanDefinition;
import io.basc.framework.db.Configurable;
import io.basc.framework.db.Database;
import io.basc.framework.db.Database;
import io.basc.framework.db.DataBaseResolver;
import io.basc.framework.db.DefaultDB;
import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.template.dialect.SqlDialect;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.StringUtils;

public class DataBaseDefinition extends FactoryBeanDefinition {

	public DataBaseDefinition(BeanFactory beanFactory) {
		super(beanFactory, Database.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(ConnectionFactory.class) && getBeanFactory().isInstance(Configurable.class);
	}

	@Override
	public Object create() throws InstanceException {
		ConnectionFactory connectionFactory = getBeanFactory().getInstance(ConnectionFactory.class);
		Configurable configurable = getBeanFactory().getInstance(Configurable.class);
		SqlDialect sqlDialect = null;
		if (StringUtils.isNotEmpty(configurable.getSqlDialect())) {
			sqlDialect = (SqlDialect) getBeanFactory().getInstance(configurable.getSqlDialect());
		}

		if (configurable.isAutoCreateDataBase()) {
			Database dataBase = null;
			if (getBeanFactory().isInstance(Database.class)) {
				dataBase = getBeanFactory().getInstance(Database.class);
			} else if (StringUtils.isNotEmpty(configurable.getUrl())) {
				if (getBeanFactory().isInstance(DataBaseResolver.class)) {
					dataBase = getBeanFactory().getInstance(DataBaseResolver.class).resolve(
							configurable.getDriverClassName(), configurable.getUrl(), configurable.getUsername(),
							configurable.getPassword());
				}
			}

			if (dataBase != null) {
				dataBase.create();
			}
		}

		if (sqlDialect == null && getBeanFactory().isInstance(SqlDialect.class)) {
			sqlDialect = getBeanFactory().getInstance(SqlDialect.class);
		}

		if (sqlDialect == null) {
			throw new UnsupportedException(SqlDialect.class.getName());
		}

		Database db = new DefaultDB(connectionFactory, sqlDialect);
		if (StringUtils.isNotEmpty(configurable.getAutoCreateTables())) {
			db.createTables(configurable.getAutoCreateTables(), configurable.isRegisterManager());
		}
		return db;
	}
}
