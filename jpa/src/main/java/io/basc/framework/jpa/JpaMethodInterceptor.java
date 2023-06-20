package io.basc.framework.jpa;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Executables;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.reflect.ReflectionMethodExecutor;
import io.basc.framework.util.Elements;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class JpaMethodInterceptor implements ExecutionInterceptor {
	private final EntityManagerFactory entityManagerFactory;
	private final Class<?> repositoryClass;

	public JpaMethodInterceptor(EntityManagerFactory entityManagerFactory, Class<?> repositoryClass) {
		this.entityManagerFactory = entityManagerFactory;
		this.repositoryClass = repositoryClass;
	}

	@Override
	public Object intercept(Executables source, Executor executor, Elements<? extends Object> args) throws Throwable {
		if(!(executor instanceof ReflectionMethodExecutor)) {
			return executor.execute(args);
		}
		
		ReflectionMethodExecutor methodExecutor = (ReflectionMethodExecutor) executor;
		Method method = methodExecutor.getExecutable();
		
		if (Modifier.isAbstract(executor.getMethod().getModifiers())) {
			EntityManager entityManager = null;
			try {
				entityManager = entityManagerFactory.createEntityManager();
				return JpaUtils.execute(entityManager, repositoryClass, invoker.getMethod(), args);
			} finally {
				if (entityManager != null) {
					entityManager.close();
				}
			}
		}
		return invoker.invoke(args);
	}

}
