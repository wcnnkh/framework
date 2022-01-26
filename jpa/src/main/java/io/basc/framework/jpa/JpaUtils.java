package io.basc.framework.jpa;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import io.basc.framework.core.reflect.ReflectionUtils;

public class JpaUtils {
	private JpaUtils() {
	};

	public static Object execute(EntityManager entityManager, Class<?> repositoryClass, Method method, Object[] args)
			throws NoSuchMethodException {
		Method entityManagerMethod = EntityManager.class.getMethod(method.getName(), method.getParameterTypes());
		return ReflectionUtils.invoke(entityManagerMethod, entityManager, args);
	}
}
