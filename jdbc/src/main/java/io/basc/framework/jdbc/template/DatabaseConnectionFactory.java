package io.basc.framework.jdbc.template;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.SimpleSql;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.element.Elements;

/**
 * 数据库连接工厂
 */
public interface DatabaseConnectionFactory extends ConnectionFactory {
	static final Sql QUERY_DATABASE_NAME_SQL = new SimpleSql("SELECT DATABASE()");

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
	 * 获取数据库服务器连接工厂
	 * 
	 * @return 可能为空，不一定支持
	 */
	@Nullable
	ConnectionFactory getServerConnectionFactory();
}
