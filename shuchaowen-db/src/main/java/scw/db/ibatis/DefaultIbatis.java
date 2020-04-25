package scw.db.ibatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE, value = Ibatis.class)
@Bean(proxy = false)
public class DefaultIbatis implements Ibatis {
	private final SqlSessionFactory sqlSessionFactory;

	public DefaultIbatis(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public SqlSession getSqlSession() {
		return IbatisUtils.getTransactionSqlSession(getSqlSessionFactory());
	}

}
