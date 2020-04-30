package scw.db.ibatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE, value = Mybatis.class)
@Bean(proxy = false)
public class DefaultMybatis implements Mybatis {
	private final SqlSessionFactory sqlSessionFactory;

	public DefaultMybatis(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public SqlSession getTransactionSqlSession() {
		return MybatisUtils.getTransactionSqlSession(getSqlSessionFactory());
	}

}
