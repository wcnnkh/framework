package io.basc.framework.jdbc.template;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.element.Elements;

/**
 * 数据库连接工厂
 */
public interface DatabaseConnectionFactory extends ConnectionFactory {
	/**
	 * 当前数据库名称
	 * 
	 * @return
	 */
	String getDatabaseName();

	/**
	 * 可选的数据库名称
	 * 
	 * @return
	 */
	Elements<String> getDatabaseNames();

	/**
	 * 得到新的数据库连接工厂
	 * 
	 * @param databaseName
	 * @return
	 * @throws UnsupportedException
	 */
	DatabaseConnectionFactory newDatabase(String databaseName) throws UnsupportedException;

	/**
	 * 获取数据库方言
	 * 
	 * @return
	 */
	DatabaseDialect getDatabaseDialect();
}
