package io.basc.framework.jpa;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import io.basc.framework.core.reflect.ReflectionUtils;

public class JpaUtils {
	private JpaUtils(){};
	
	public static Object execute(EntityManager entityManager, Class<?> repositoryClass, Method method, Object[] args) throws NoSuchMethodException, SecurityException{
		Method entityManagerMethod = EntityManager.class.getMethod(method.getName(), method.getParameterTypes());
		if(entityManagerMethod == null){
			throw new NoSuchMethodException(method.toString());
		}
		
		return ReflectionUtils.invokeMethod(entityManagerMethod, entityManager, args);
	}
}
