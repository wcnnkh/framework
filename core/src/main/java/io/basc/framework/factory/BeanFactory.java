package io.basc.framework.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.basc.framework.aop.Aop;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;

public interface BeanFactory
		extends ServiceLoaderFactory, BeanDefinitionFactory, SingletonFactory, BeanLifeCycleManager {
	@Nullable
	BeanFactory getParentBeanFactory();

	Aop getAop();

	@SuppressWarnings("unchecked")
	default <T> Map<String, T> getBeans(Class<? extends T> type) {
		return (Map<String, T>) getBeans(ResolvableType.forClass(type));
	}

	default Map<String, Object> getBeans(ResolvableType type) {
		String[] names = getDefinitionIds();
		if (names == null || names.length == 0) {
			return Collections.emptyMap();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		for (String name : names) {
			BeanDefinition definition = getDefinition(name);
			if (definition == null) {
				continue;
			}

			if (!definition.getTypeDescriptor().getResolvableType().isAssignableFrom(type)) {
				continue;
			}

			map.put(name, getInstance(name));
		}
		return map;
	}

	boolean isSingleton(Class<?> clazz);

	boolean isSingleton(String name);

	BeanResolver getBeanResolver();

	Object getInstance(String name, Object... params) throws FactoryException;

	boolean isInstance(Class<?> clazz, Object... params);

	boolean isInstance(String name, Class<?>... parameterTypes);

	boolean isInstance(String name, Object... params);

	<T> T getInstance(Class<? extends T> clazz, Class<?>[] parameterTypes, Object... params) throws FactoryException;

	<T> T getInstance(Class<? extends T> clazz, Object... params) throws FactoryException;

	Object getInstance(String name, Class<?>[] parameterTypes, Object... params) throws FactoryException;

	boolean isInstance(Class<?> clazz, Class<?>... parameterTypes);
}
