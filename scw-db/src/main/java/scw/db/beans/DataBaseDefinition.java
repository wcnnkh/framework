package scw.db.beans;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.core.utils.StringUtils;
import scw.db.Configurable;
import scw.db.DB;
import scw.db.DBUtils;
import scw.db.DefaultDB;
import scw.db.database.DataBase;
import scw.instance.InstanceException;
import scw.mysql.MysqlDialect;
import scw.orm.sql.SqlDialect;
import scw.sql.ConnectionFactory;

public class DataBaseDefinition extends DefaultBeanDefinition {

	public DataBaseDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, DB.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(ConnectionFactory.class) && beanFactory.isInstance(Configurable.class);
	}

	@Override
	public Object create() throws InstanceException {
		ConnectionFactory connectionFactory = beanFactory.getInstance(ConnectionFactory.class);
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
				dataBase = DBUtils.automaticRecognition(configurable.getDriverClassName(), configurable.getUrl(),
						configurable.getUsername(), configurable.getPassword());
			}

			if (dataBase != null) {
				dataBase.create();

				if (sqlDialect == null) {
					sqlDialect = dataBase.getSqlDialect();
				}
			}
		}

		if (sqlDialect == null && beanFactory.isInstance(SqlDialect.class)) {
			sqlDialect = beanFactory.getInstance(SqlDialect.class);
		}
		
		if(sqlDialect == null) {
			sqlDialect = new MysqlDialect();
		}

		DB db = new DefaultDB(connectionFactory, sqlDialect);
		if (StringUtils.isNotEmpty(configurable.getAutoCreateTables())) {
			db.createTables(configurable.getAutoCreateTables(), configurable.isRegisterManager());
		}
		return db;
	}
}
