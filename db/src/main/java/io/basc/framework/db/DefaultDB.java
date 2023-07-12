package io.basc.framework.db;

import io.basc.framework.context.ClassScanner;
import io.basc.framework.context.config.support.DefaultClassScanner;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.sql.template.annotation.Table;
import io.basc.framework.sql.template.dialect.SqlDialect;
import io.basc.framework.sql.template.support.DefaultSqlTemplate;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ServiceLoader;

public class DefaultDB extends DefaultSqlTemplate implements DB {
	private ClassScanner classScanner = new DefaultClassScanner();

	public DefaultDB(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		super(connectionFactory, sqlDialect);
	}

	public ClassScanner getClassScanner() {
		return classScanner;
	}

	public void setClassScanner(ClassScanner classScanner) {
		Assert.requiredArgument(classScanner != null, "classScanner");
		this.classScanner = classScanner;
	}

	public void createTable(Class<?> tableClass, boolean registerManager) {
		createTable(tableClass, null, registerManager);
	}

	@Override
	public void createTable(Class<?> entityClass, String tableName) {
		DBManager.register(entityClass, this);
		super.createTable(entityClass, tableName);
	}

	public void createTable(Class<?> tableClass, String tableName, boolean registerManager) {
		if (registerManager) {
			DBManager.register(tableClass, this);
		}

		super.createTable(tableClass, tableName);
	}

	public void createTables(String packageName, boolean registerManager) {
		ServiceLoader<Class<?>> classesLoader = getClassScanner().scan(packageName, null, (e, m) -> {
			return e.getAnnotationMetadata().hasAnnotation(Table.class.getName());
		});

		for (Class<?> tableClass : classesLoader.getServices()) {
			Table table = tableClass.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			if (registerManager) {
				DBManager.register(tableClass, this);
			}

			createTable(tableClass, false);
		}
	}
}
