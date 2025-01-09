package io.basc.framework.ibatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.ibatis.session.SqlSession;

import io.basc.framework.util.collection.ArrayUtils;

public class SqlSessionProxyInvocationHandler implements InvocationHandler{
	private final SqlSession sqlSession;
	
	public SqlSessionProxyInvocationHandler(SqlSession sqlSession){
		this.sqlSession = sqlSession;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if(ArrayUtils.isEmpty(args)){
			if(method.getName().equals("close")){
				return null;
			}else if(method.getName().equals(SqlSessionProxy.TARGET_SQL_SESSION_METHOD)){
				return sqlSession;
			}
		}
		
		return method.invoke(sqlSession, args);
	}
}
