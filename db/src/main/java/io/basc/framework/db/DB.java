package io.basc.framework.db;

import io.basc.framework.sql.orm.SqlTemplate;

public interface DB extends SqlTemplate {
	void createTable(Class<?> tableClass, boolean registerManager);

	void createTable(Class<?> tableClass, String tableName, boolean registerManager);

	default void createTables(String packageName) {
		createTables(packageName, true);
	}

	void createTables(String packageName, boolean registerManager);
}
