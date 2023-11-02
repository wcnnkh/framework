package io.basc.framework.jdbc.template;

import io.basc.framework.lang.Nullable;

/**
 * jdbc的url定义
 * 
 * @author wcnnkh
 *
 */
public interface DatabaseURL extends Cloneable {

	String getProtocol();

	String getHost();

	/**
	 * 获取端口号
	 * 
	 * @return -1表示无
	 */
	int getProt();

	@Nullable
	String getDatabaseNmae();

	/**
	 * 设置数据库名称
	 * 
	 * @param databaseName
	 */
	void setDatabaseName(@Nullable String databaseName);

	@Nullable
	String getQuery();

	DatabaseURL clone();

	String getRawURL();
}
