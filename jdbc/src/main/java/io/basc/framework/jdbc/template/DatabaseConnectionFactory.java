package io.basc.framework.jdbc.template;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.collections.Elements;

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
	 * @return 如果不存在或不支持可能为空
	 */
	@Nullable
	DatabaseConnectionFactory getDatabaseConnectionFactory(String databaseName);

	/**
	 * 获取数据库方言
	 * 
	 * @return
	 */
	DatabaseDialect getDatabaseDialect();

	/**
	 * 获取驱动类名
	 * 
	 * @return
	 */
	@Nullable
	String getDriverClassName();
}
