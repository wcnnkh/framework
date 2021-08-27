package io.basc.framework.ibatis;

import org.apache.ibatis.session.SqlSessionFactory;

public interface SqlSessionFactoryProxy extends SqlSessionFactory {
	public static final String TARGET_SQL_SESSION_FACTORY_METHOD = "getTargetSqlSessionFactory";

	SqlSessionFactory getTargetSqlSessionFactory();
}
