package io.basc.framework.db;

import io.basc.framework.orm.sql.SqlTemplate;

public interface DB extends SqlTemplate {
	/**
	 * 创建表
	 * 
	 * @param tableClass
	 * @param registerManager 是否注册到{@see DBManager}
	 * @return
	 */
	void createTable(Class<?> tableClass, boolean registerManager);

	/**
	 * 创建表
	 * 
	 * @param tableClass
	 * @param tableName       指定表名
	 * @param registerManager 是否注册到{@see DBManager}
	 * @return
	 */
	void createTable(Class<?> tableClass, String tableName, boolean registerManager);

	default void createTables(String packageName) {
		createTables(packageName, true);
	}

	/**
	 * 扫描指定包下的@Table并创建表
	 * 
	 * @param packageName
	 * @param registerManager 是否注册到{@see DBManager}
	 */
	void createTables(String packageName, boolean registerManager);
}
