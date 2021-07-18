package scw.jpa;

import java.lang.reflect.Modifier;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import scw.aop.MethodInterceptor;
import scw.core.reflect.MethodInvoker;

public class JpaMethodInterceptor implements MethodInterceptor {
	private final EntityManagerFactory entityManagerFactory;
	private final Class<?> repositoryClass;

	public JpaMethodInterceptor(EntityManagerFactory entityManagerFactory,
			Class<?> repositoryClass) {
		this.entityManagerFactory = entityManagerFactory;
		this.repositoryClass = repositoryClass;
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args)
			throws Throwable {
		if (Modifier.isAbstract(invoker.getMethod().getModifiers())) {
			EntityManager entityManager = null;
			try {
				entityManager = entityManagerFactory.createEntityManager();
				return JpaUtils.execute(entityManager, repositoryClass,
						invoker.getMethod(), args);
			} finally {
				if (entityManager != null) {
					entityManager.close();
				}
			}
		}
		return invoker.invoke(args);
	}

}
