package io.basc.framework.ibatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import io.basc.framework.util.collection.ArrayUtils;

public class SqlSessionFactoryProxyInvocationHandler implements InvocationHandler {
	private final SqlSessionFactory sqlSessionFactory;

	public SqlSessionFactoryProxyInvocationHandler(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getReturnType() == SqlSession.class) {
			return MybatisUtils.getTransactionSqlSession(sqlSessionFactory, (transaction) -> {
				try {
					return (SqlSession) method.invoke(sqlSessionFactory, args);
				} catch (Throwable e) {
					throw new IbatisException(e);
				}
			});
		}

		if (ArrayUtils.isEmpty(args)) {
			if (method.getName().equals(SqlSessionFactoryProxy.TARGET_SQL_SESSION_FACTORY_METHOD)) {
				return sqlSessionFactory;
			}
		}
		return method.invoke(sqlSessionFactory, args);
	}

}
