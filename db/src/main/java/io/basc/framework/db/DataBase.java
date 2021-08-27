package io.basc.framework.db;

import io.basc.framework.sql.ConnectionFactory;

/**
 * 数据库
 * 
 * @author shuchaowen
 *
 */
public interface DataBase extends ConnectionFactory {
	String getUrl();

	/**
	 * 获取数据库名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 获取数据库驱动
	 * 
	 * @return
	 */
	String getDriverClassName();

	/**
	 * 获取连接数据库的账号
	 * 
	 * @return
	 */
	String getUsername();

	/**
	 * 获取连接数据库的密码
	 * 
	 * @return
	 */
	String getPassword();

	/**
	 * 创建数据库
	 */
	default void create() {
		create(getName());
	}

	/**
	 * 创建数据库
	 * 
	 * @param database
	 */
	void create(String database);
}
