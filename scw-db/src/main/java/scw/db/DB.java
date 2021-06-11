package scw.db;

import scw.orm.sql.SqlTemplate;

public interface DB extends SqlTemplate {
	/**
	 * 创建表
	 * 
	 * @param tableClass
	 * @param registerManager 是否注册到{@see DBManager}
	 * @return
	 */
	boolean createTable(Class<?> tableClass, boolean registerManager);

	/**
	 * 创建表
	 * 
	 * @param tableClass
	 * @param tableName       指定表名
	 * @param registerManager 是否注册到{@see DBManager}
	 * @return
	 */
	boolean createTable(Class<?> tableClass, String tableName, boolean registerManager);

	default void createTable(String packageName) {
		createTable(packageName, true);
	}

	/**
	 * 扫描指定包下的@Table并创建表
	 * 
	 * @param packageName
	 * @param registerManager 是否注册到{@see DBManager}
	 */
	void createTable(String packageName, boolean registerManager);
}
