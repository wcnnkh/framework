package io.basc.framework.jpa;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.reflect.MethodExecutor;
import io.basc.framework.util.Elements;

public class JpaMethodInterceptor implements ExecutionInterceptor {
	private final EntityManagerFactory entityManagerFactory;
	private final Class<?> repositoryClass;

	public JpaMethodInterceptor(EntityManagerFactory entityManagerFactory, Class<?> repositoryClass) {
		this.entityManagerFactory = entityManagerFactory;
		this.repositoryClass = repositoryClass;
	}

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (!(executor instanceof MethodExecutor)) {
			return executor.execute(args);
		}

		MethodExecutor methodExecutor = (MethodExecutor) executor;
		Method method = methodExecutor.getExecutable();
		if (Modifier.isAbstract(method.getModifiers())) {
			EntityManager entityManager = null;
			try {
				entityManager = entityManagerFactory.createEntityManager();
				return JpaUtils.execute(entityManager, repositoryClass, method, args.toArray());
			} finally {
				if (entityManager != null) {
					entityManager.close();
				}
			}
		}
		return executor.execute(args);
	}

}
