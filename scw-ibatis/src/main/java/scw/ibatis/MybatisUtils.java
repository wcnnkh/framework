package scw.ibatis;

import java.lang.reflect.Proxy;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public final class MybatisUtils {
	private MybatisUtils() {
	};
	
	public static SqlSession getTransactionSqlSession(SqlSessionFactory sqlSessionFactory){
		return getTransactionSqlSession(TransactionManager.GLOBAL, sqlSessionFactory);
	}

	public static SqlSession getTransactionSqlSession(TransactionManager transactionManager,
			SqlSessionFactory sqlSessionFactory) {
		Transaction transaction = transactionManager.getTransaction();
		if (transaction == null) {
			return sqlSessionFactory.openSession(true);
		}

		MybatisTransactionResource resource = transaction
				.getResource(sqlSessionFactory);
		if (resource == null) {
			MybatisTransactionResource mybatisTransactionResource = new MybatisTransactionResource(sqlSessionFactory,
					transaction.isActive());
			resource = transaction.bindResource(sqlSessionFactory, mybatisTransactionResource);
			if(resource == null){
				resource = mybatisTransactionResource;
			}
		}
		return resource.getSqlSession();
	}

	public static SqlSession proxySqlSession(SqlSession sqlSession) {
		if (sqlSession == null) {
			return null;
		}

		if (sqlSession instanceof SqlSessionProxy) {
			return sqlSession;
		}

		return (SqlSession) Proxy.newProxyInstance(
				SqlSessionProxy.class.getClassLoader(),
				new Class<?>[] { SqlSessionProxy.class },
				new SqlSessionProxyInvocationHandler(sqlSession));
	}

	public static void closeSqlSessionProxy(SqlSession sqlSession) {
		if (sqlSession == null) {
			return;
		}

		if (sqlSession instanceof SqlSessionProxy) {
			((SqlSessionProxy) sqlSession).getTargetSqlSession().close();
			return;
		}
		sqlSession.close();
	}
}
