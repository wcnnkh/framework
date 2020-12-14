package scw.ibatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public interface Mybatis {
	SqlSessionFactory getSqlSessionFactory();

	SqlSession getTransactionSqlSession();
}