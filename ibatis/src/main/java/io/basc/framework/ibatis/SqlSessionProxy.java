package io.basc.framework.ibatis;

import org.apache.ibatis.session.SqlSession;

public interface SqlSessionProxy extends SqlSession{
	public static final String TARGET_SQL_SESSION_METHOD = "getTargetSqlSession";
	
	SqlSession getTargetSqlSession();
}
