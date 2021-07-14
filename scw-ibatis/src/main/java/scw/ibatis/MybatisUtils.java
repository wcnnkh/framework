package scw.ibatis;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import scw.aop.support.ProxyUtils;
import scw.core.reflect.MethodInvoker;
import scw.transaction.Transaction;
import scw.transaction.TransactionUtils;
import scw.util.stream.Processor;

public final class MybatisUtils {
	private MybatisUtils() {
	};

	public static SqlSession getTransactionSqlSession(SqlSessionFactory sqlSessionFactory,
			Supplier<SqlSession> sqlSessionSupplier) {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			return sqlSessionSupplier.get();
		}

		MybatisTransactionResource resource = transaction.getResource(sqlSessionFactory);
		if (resource == null) {
			MybatisTransactionResource mybatisTransactionResource = new MybatisTransactionResource(sqlSessionSupplier);
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
	public static <T> T proxyMapper(SqlSessionFactory sqlSessionFactory, Class<? extends T> mapperClass,
			Processor<scw.aop.Proxy, Object, IbatisException> processor,
			Processor<MethodInvoker, SqlSession, Throwable> openSessionProcessor) {
		scw.aop.Proxy proxy = ProxyUtils.getFactory().getProxy(mapperClass, null,
				new MapperMethodInterceptor(sqlSessionFactory, openSessionProcessor));
		return (T) processor.process(proxy);
	}
}
