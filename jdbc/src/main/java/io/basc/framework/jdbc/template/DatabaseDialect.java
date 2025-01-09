package io.basc.framework.jdbc.template;

import io.basc.framework.jdbc.ConnectionOperations;
import io.basc.framework.util.collection.Elements;

public interface DatabaseDialect extends SqlDialect {
	Elements<String> getDatabaseNames(ConnectionOperations operations);

	/**
	 * 获取当前选择的数据库
	 * 
	 * @param operations
	 * @return
	 */
	String getSelectedDatabaseName(ConnectionOperations operations);

	/**
	 * 创建数据库
	 * 
	 * @param operations
	 * @param databaseName
	 */
	void createDatabase(ConnectionOperations operations, String databaseName);

	/**
	 * 解析url
	 * 
	 * @param url
	 * @return
	 */
	DatabaseURL resolveUrl(String url);
}
