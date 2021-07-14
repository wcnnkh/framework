package scw.ibatis;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import scw.aop.MethodInterceptor;
import scw.convert.TypeDescriptor;
import scw.core.reflect.MethodInvoker;
import scw.util.stream.Processor;
import scw.util.stream.StreamProcessorSupport;

class MapperMethodInterceptor implements MethodInterceptor {
	private final SqlSessionFactory sqlSessionFactory;
	private final Processor<MethodInvoker, SqlSession, Throwable> openSessionProcessor;

	public MapperMethodInterceptor(SqlSessionFactory sqlSessionFactory,
			Processor<MethodInvoker, SqlSession, Throwable> openSessionProcessor) {
		this.sqlSessionFactory = sqlSessionFactory;
		this.openSessionProcessor = openSessionProcessor;
	}

	private SqlSession openSession(MethodInvoker invoker) throws Throwable {
		return openSessionProcessor.process(invoker);
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		if (invoker.getDeclaringClass().isInterface()) {
			SqlSession sqlSession = null;
			try {
				sqlSession = openSession(invoker);
				Object mapper = sqlSession.getMapper(invoker.getDeclaringClass());
				return invoker.getMethod().invoke(mapper, args);
			} finally {
				if (sqlSession != null) {
					sqlSession.close();
				}
			}
		}

		if (Modifier.isAbstract(invoker.getMethod().getModifiers())) {
			Method method = invoker.getMethod();
			MappedStatement mappedStatement = sqlSessionFactory.getConfiguration().getMappedStatement(method.getName());
			if (mappedStatement == null) {
				throw new IbatisException("not found mapped statement:" + method.getName());
			}

			TypeDescriptor returnType = TypeDescriptor.forMethodReturnType(method);
			SqlSession sqlSession = null;
			try {
				sqlSession = openSession(invoker);
				return exeucte(sqlSession, returnType, mappedStatement, method, args);
			} finally {
				if (sqlSession != null) {
					sqlSession.close();
				}
			}
		}
		return invoker.invoke(args);
	}

	private Object exeucte(SqlSession sqlSession, TypeDescriptor returnType, MappedStatement mappedStatement,
			Method method, Object parameter) {
		if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
			if (returnType.isCollection()) {
				return sqlSession.selectList(mappedStatement.getId(), parameter);
			} else if (returnType.getType() == Cursor.class) {
				return sqlSession.selectCursor(mappedStatement.getId(), parameter);
			} else if (returnType.getType() == scw.util.stream.Cursor.class) {
				Cursor<?> cursor = sqlSession.selectCursor(mappedStatement.getId(), parameter);
				Stream<?> stream = StreamSupport.stream(cursor.spliterator(), false).onClose(() -> {
					try {
						cursor.close();
					} catch (IOException e) {
						throw new IbatisException(e);
					}
				});
				return StreamProcessorSupport.cursor(stream);
			} else {
				return sqlSession.selectOne(mappedStatement.getId(), parameter);
			}
		} else if (mappedStatement.getSqlCommandType() == SqlCommandType.DELETE) {
			int value = sqlSession.delete(mappedStatement.getId(), parameter);
			if (returnType.getType() == Void.class) {
				return null;
			}
			return value;
		} else if (mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE) {
			int value = sqlSession.update(mappedStatement.getId(), parameter);
			if (returnType.getType() == Void.class) {
				return null;
			}

			return value;
		} else if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT) {
			int value = sqlSession.insert(mappedStatement.getId(), parameter);
			if (returnType.getType() == Void.class) {
				return null;
			}

			return value;
		}
		throw new IbatisException(mappedStatement.getSqlSource().toString());
	}
}
