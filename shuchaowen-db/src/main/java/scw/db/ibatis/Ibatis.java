package scw.db.ibatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public interface Ibatis {
	SqlSessionFactory getSqlSessionFactory();

	SqlSession getSqlSession();
}