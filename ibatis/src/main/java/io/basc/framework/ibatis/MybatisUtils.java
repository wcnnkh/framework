package io.basc.framework.ibatis;

import java.lang.reflect.Proxy;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import io.basc.framework.execution.aop.Aop;
import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionUtils;
import io.basc.framework.util.function.Processor;

public final class MybatisUtils {
	private MybatisUtils() {
	};

	public static SqlSession getTransactionSqlSession(SqlSessionFactory sqlSessionFactory,
			OpenSessionProcessor openSessionProcessor) {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			return openSessionProcessor.process(transaction);
		}

		SqlSessionTransactionResource resource = transaction.getResource(sqlSessionFactory);
		if (resource == null) {
			resource = new SqlSessionTransactionResource(transaction, openSessionProcessor);
			transaction.registerResource(sqlSessionFactory, resource);
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

		return (SqlSession) Proxy.newProxyInstance(SqlSessionProxy.class.getClassLoader(),
				new Class<?>[] { SqlSessionProxy.class }, new SqlSessionProxyInvocationHandler(sqlSession));
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

	public static SqlSessionFactory proxySqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		if (sqlSessionFactory == null) {
			return null;
		}

		if (sqlSessionFactory instanceof SqlSessionFactoryProxy) {
			return sqlSessionFactory;
		}

		return (SqlSessionFactory) Proxy.newProxyInstance(SqlSessionFactoryProxy.class.getClassLoader(),
				new Class<?>[] { SqlSessionFactoryProxy.class },
				new SqlSessionFactoryProxyInvocationHandler(sqlSessionFactory));
	}

	@SuppressWarnings("unchecked")
	public static <T> T proxyMapper(Aop aop, Class<? extends T> mapperClass,
			Processor<io.basc.framework.execution.aop.Proxy, Object, IbatisException> processor,
			Processor<? super ReflectionMethod, SqlSession, Throwable> openSessionProcessor) {
		io.basc.framework.execution.aop.Proxy proxy = aop.getProxy(mapperClass, null,
				new MapperMethodInterceptor(mapperClass, openSessionProcessor));
		return (T) processor.process(proxy);
	}
}
