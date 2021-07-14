package scw.ibatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import scw.core.utils.ArrayUtils;

public class SqlSessionFactoryProxyInvocationHandler implements InvocationHandler {
	private final SqlSessionFactory sqlSessionFactory;

	public SqlSessionFactoryProxyInvocationHandler(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getReturnType() == SqlSession.class) {
			return MybatisUtils.getTransactionSqlSession(sqlSessionFactory, () -> {
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
