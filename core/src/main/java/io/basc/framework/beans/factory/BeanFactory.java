package io.basc.framework.beans.factory;

import io.basc.framework.beans.BeansException;
import io.basc.framework.core.ResolvableType;

public interface BeanFactory {
	Scope getScope();

	boolean containsBean(String name);

	boolean isFactoryBean(String name);

	Object getBean(String name) throws BeansException;

	FactoryBean<? extends Object> getFactoryBean(String beanName) throws NoSuchBeanDefinitionException;

	@SuppressWarnings("unchecked")
	default <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		if (!isTypeMatch(name, requiredType)) {
			throw new BeanNotOfRequiredTypeException(name, requiredType, getType(name));
		}
		return (T) getBean(name);
	}

	<T> T getBean(Class<? extends T> requiredType) throws BeansException, NoUniqueBeanDefinitionException;

	Object getBean(ResolvableType requiredType) throws BeansException, NoUniqueBeanDefinitionException;

	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
}
