package io.basc.framework.ibatis;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.util.function.Processor;

public class MapperMethodInterceptor implements MethodInterceptor {
	private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<Method, MapperMethod>();
	private final Processor<? super MethodInvoker, ? extends SqlSession, ? extends Throwable> openSessionProcessor;
	private final Class<?> mapperClass;

	public MapperMethodInterceptor(Class<?> mapperClass,
			Processor<? super MethodInvoker, ? extends SqlSession, ? extends Throwable> openSessionProcessor) {
		this.mapperClass = mapperClass;
		this.openSessionProcessor = openSessionProcessor;
	}

	private SqlSession openSession(MethodInvoker invoker) throws Throwable {
		return openSessionProcessor.process(invoker);
	}

	private MapperMethod cachedMapperMethod(Method method, SqlSession sqlSession) {
		MapperMethod mapperMethod = methodCache.get(method);
		if (mapperMethod == null) {
			MapperMethod created = new MapperMethod(mapperClass, method, sqlSession.getConfiguration());
			mapperMethod = methodCache.putIfAbsent(method, created);
			if (mapperMethod == null) {
				mapperMethod = created;
			}
		}
		return mapperMethod;
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		if (Modifier.isAbstract(invoker.getMethod().getModifiers())) {
			Method method = invoker.getMethod();
			SqlSession sqlSession = null;
			try {
				sqlSession = openSession(invoker);
				MapperMethod mapperMethod = cachedMapperMethod(method, sqlSession);
				return mapperMethod.execute(sqlSession, args);
			} finally {
				if (sqlSession != null) {
					sqlSession.close();
				}
			}
		}
		return invoker.invoke(args);
	}
}
