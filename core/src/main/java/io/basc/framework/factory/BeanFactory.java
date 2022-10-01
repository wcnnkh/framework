package io.basc.framework.factory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.aop.Aop;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;

public interface BeanFactory
		extends ServiceLoaderFactory, BeanDefinitionFactory, SingletonFactory, BeanLifeCycleManager {
	@Nullable
	BeanFactory getParentBeanFactory();

	Aop getAop();

	default Map<String, Object> getBeans(ResolvableType type) {
		return matchType(type).filter((e) -> isInstance(e.getId()))
				.collect(Collectors.toMap((e) -> e.getId(), (e) -> getInstance(e.getId())));
	}

	default Map<String, Object> getBeans(Type type) {
		return getBeans(ResolvableType.forType(type));
	}

	@SuppressWarnings("unchecked")
	default <T> Map<String, T> getBeans(Class<? extends T> type) {
		return (Map<String, T>) getBeans(ResolvableType.forClass(type));
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
