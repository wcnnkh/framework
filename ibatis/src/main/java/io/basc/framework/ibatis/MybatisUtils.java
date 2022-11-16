package io.basc.framework.ibatis;

import java.lang.reflect.Proxy;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionUtils;
import io.basc.framework.util.Processor;

public final class MybatisUtils {
	private MybatisUtils() {
	};

	public static SqlSession getTransactionSqlSession(SqlSessionFactory sqlSessionFactory, OpenSessionProcessor openSessionProcessor) {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			return openSessionProcessor.process(transaction);
		}

		SqlSessionTransactionResource resource = transaction.getResource(sqlSessionFactory);
		if (resource == null) {
			SqlSessionTransactionResource mybatisTransactionResource = new SqlSessionTransactionResource(transaction, openSessionProcessor);
			resource = transaction.bindResource(sqlSessionFactory, mybatisTransactionResource);
			if (resource == null) {
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
	public static <T> T proxyMapper(Class<? extends T> mapperClass,
			Processor<io.basc.framework.aop.Proxy, Object, IbatisException> processor,
			Processor<MethodInvoker, SqlSession, Throwable> openSessionProcessor) {
		io.basc.framework.aop.Proxy proxy = ProxyUtils.getFactory().getProxy(mapperClass, null,
				new MapperMethodInterceptor(mapperClass, openSessionProcessor));
		return (T) processor.process(proxy);
	}
}
