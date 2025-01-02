package io.basc.framework.ibatis;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.aop.ExecutionInterceptor;
import io.basc.framework.core.execution.reflect.ReflectionMethod;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Function;

public class MapperMethodInterceptor implements ExecutionInterceptor {
	private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<Method, MapperMethod>();
	private final Function<? super ReflectionMethod, ? extends SqlSession, ? extends Throwable> openSessionProcessor;
	private final Class<?> mapperClass;

	public MapperMethodInterceptor(Class<?> mapperClass,
			Function<? super ReflectionMethod, ? extends SqlSession, ? extends Throwable> openSessionProcessor) {
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

	public Object intercept(ReflectionMethod executor, Elements<? extends Object> args) throws Throwable {
		Method method = executor.getMember();
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

	@Override
	public Object intercept(Function function, Elements<? extends Object> args) throws Throwable {
		if (function instanceof ReflectionMethod) {
			ReflectionMethod reflectionMethod = (ReflectionMethod) function;
			return intercept(reflectionMethod, args);
		}
		return function.execute(args);
	}
}
