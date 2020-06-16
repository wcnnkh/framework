package scw.db.ibatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface Mybatis {
	SqlSessionFactory getSqlSessionFactory();

	SqlSession getTransactionSqlSession();
}