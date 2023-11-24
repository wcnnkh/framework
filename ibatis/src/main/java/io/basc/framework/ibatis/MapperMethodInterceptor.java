package io.basc.framework.ibatis;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;

import io.basc.framework.execution.reflect.ReflectionMethodExecutionInterceptor;
import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.Processor;

public class MapperMethodInterceptor implements ReflectionMethodExecutionInterceptor {
	private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<Method, MapperMethod>();
	private final Processor<? super ReflectionMethod, ? extends SqlSession, ? extends Throwable> openSessionProcessor;
	private final Class<?> mapperClass;

	public MapperMethodInterceptor(Class<?> mapperClass,
			Processor<? super ReflectionMethod, ? extends SqlSession, ? extends Throwable> openSessionProcessor) {
		this.mapperClass = mapperClass;
		this.openSessionProcessor = openSessionProcessor;
	}

	private SqlSession openSession(ReflectionMethod invoker) throws Throwable {
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
	public Object intercept(ReflectionMethod executor, Elements<Object> args) throws Throwable {
		Method method = executor.getExecutable();
		if (Modifier.isAbstract(method.getModifiers())) {
			SqlSession sqlSession = null;
			try {
				sqlSession = openSession(executor);
				MapperMethod mapperMethod = cachedMapperMethod(method, sqlSession);
				return mapperMethod.execute(sqlSession, args.toArray());
			} finally {
				if (sqlSession != null) {
					sqlSession.close();
				}
			}
		}
		return executor.execute(args);
	}
}
