package io.basc.framework.db;

import io.basc.framework.jdbc.template.dialect.SqlDialect;
import io.basc.framework.lang.Nullable;

/**
 * 数据库方言
 * 
 * @author wcnnkh
 *
 */
public interface DatabaseDialect extends SqlDialect {
	// 解析数据库配置
	@Nullable
	DatabaseProperties resolveDatabaseProperties(DatabaseProperties properties);

	/**
	 * 后置初始化
	 * 
	 * @param properties
	 */
	String afterPropertiesSet(DatabaseProperties properties);
}
