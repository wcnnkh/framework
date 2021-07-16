package scw.ibatis;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;

import scw.aop.MethodInterceptor;
import scw.core.reflect.MethodInvoker;
import scw.util.stream.Processor;

public class MapperMethodInterceptor implements MethodInterceptor {
	private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<Method, MapperMethod>();
	private final Processor<MethodInvoker, SqlSession, Throwable> openSessionProcessor;
	private final Class<?> mapperClass;

	public MapperMethodInterceptor(Class<?> mapperClass,
			Processor<MethodInvoker, SqlSession, Throwable> openSessionProcessor) {
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
