package io.basc.framework.beans.factory;

import io.basc.framework.beans.BeansException;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.spi.ServiceLoaderDiscovery;
import lombok.NonNull;

public interface BeanFactory extends ServiceLoaderDiscovery {
	boolean containsBean(@NonNull String name);

	boolean isFactoryBean(@NonNull String name);

	Object getBean(@NonNull String name) throws BeansException;

	FactoryBean<? extends Object> getFactoryBean(@NonNull String beanName) throws NoSuchBeanDefinitionException;

	@SuppressWarnings("unchecked")
	default <T> T getBean(@NonNull String name, @NonNull Class<T> requiredType) throws BeansException {
		if (!isTypeMatch(name, requiredType)) {
			throw new BeanNotOfRequiredTypeException(name, requiredType, getType(name));
		}
		return (T) getBean(name);
	}

	@Override
	default <S> ServiceLoader<S> getServiceLoader(@NonNull Class<S> requiredType) {
		return getServiceLoader(ResolvableType.forClass(requiredType));
	}

	<S> ServiceLoader<S> getServiceLoader(@NonNull ResolvableType requiredType);

	<T> T getBean(@NonNull Class<T> requiredType) throws BeansException, NoUniqueBeanDefinitionException;

	Object getBean(@NonNull ResolvableType requiredType) throws BeansException, NoUniqueBeanDefinitionException;

	boolean isTypeMatch(@NonNull String name, @NonNull ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	boolean isTypeMatch(@NonNull String name, @NonNull Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	Class<?> getType(@NonNull String name) throws NoSuchBeanDefinitionException;

	boolean isSingleton(@NonNull String name) throws NoSuchBeanDefinitionException;
}
