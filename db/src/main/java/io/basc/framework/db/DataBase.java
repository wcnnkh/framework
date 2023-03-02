package io.basc.framework.db;

import io.basc.framework.sql.ConnectionFactory;

/**
 * 数据库
 * 
 * @author wcnnkh
 *
 */
public interface DataBase extends ConnectionFactory {
	String getUrl();

	String getName();

	String getDriverClassName();

	String getUsername();

	String getPassword();

	default void create() {
		create(getName());
	}

	void create(String database);
}
