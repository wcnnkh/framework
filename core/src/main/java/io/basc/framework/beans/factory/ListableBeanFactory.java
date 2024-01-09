package io.basc.framework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.beans.BeansException;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.element.Elements;

public interface ListableBeanFactory extends BeanFactory {
	Elements<String> getBeanNames();

	default Elements<String> getBeanNamesForType(ResolvableType requiredType) {
		return getBeanNames().filter((name) -> isTypeMatch(name, requiredType));
	}

	default Elements<String> getBeanNamesForType(Class<?> requiredType) {
		return getBeanNames().filter((name) -> isTypeMatch(name, requiredType));
	}

	Elements<String> getFactoryBeanNames();

	@SuppressWarnings("unchecked")
	@Override
	default <T> T getBean(Class<T> requiredType) throws BeansException {
		Elements<String> beanNames = getBeanNamesForType(requiredType).toList();
		if (beanNames.isEmpty()) {
			throw new NoSuchBeanDefinitionException(requiredType);
		}

		if (!beanNames.isUnique()) {
			throw new NoUniqueBeanDefinitionException(requiredType, beanNames.toList());
		}

		return (T) getBean(beanNames.first());
	}

	@Override
	default <T> BeanProvider<T> getBeanProvider(Class<T> requiredType) {
		Elements<String> names = getBeanNamesForType(requiredType);
		return new NameBeanProvider<>(names, this);
	}

	@Override
	default BeanProvider<Object> getBeanProvider(ResolvableType requiredType) {
		Elements<String> names = getBeanNamesForType(requiredType);
		return new NameBeanProvider<>(names, this);
	}

	@Override
	default Object getBean(ResolvableType requiredType) throws BeansException {
		Elements<String> beanNames = getBeanNamesForType(requiredType).toList();
		if (beanNames.isEmpty()) {
			throw new NoSuchBeanDefinitionException(requiredType);
		}

		if (!beanNames.isUnique()) {
			throw new NoUniqueBeanDefinitionException(requiredType, beanNames.toList());
		}

		return getBean(beanNames.first());
	}

	default Map<String, Object> getBeansOfType(ResolvableType requiredType) throws BeansException {
		List<String> beanNames = getBeanNamesForType(requiredType).toList();
		if (beanNames.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, Object> map = new HashMap<>(beanNames.size());
		for (String name : beanNames) {
			Object bean = getBean(name);
			map.put(name, bean);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	default <T> Map<String, T> getBeansOfType(Class<T> requiredType) throws BeansException {
		List<String> beanNames = getBeanNamesForType(requiredType).toList();
		if (beanNames.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, T> map = new HashMap<>(beanNames.size());
		for (String name : beanNames) {
			T bean = (T) getBean(name);
			map.put(name, bean);
		}
		return map;
	}

	default Elements<String> getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		return getBeanNames().filter((name) -> findAnnotationOnBean(name, annotationType) != null);
	}

	default Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
			throws BeansException {
		List<String> names = getBeanNamesForAnnotation(annotationType).toList();
		if (names.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, Object> map = new HashMap<>(names.size());
		for (String name : names) {
			Object bean = getBean(name);
			map.put(name, bean);
		}
		return map;
	}

	@Nullable
	default <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException {
		FactoryBean<?> factoryBean = getFactoryBean(beanName);
		A annotation = factoryBean.getTypeDescriptor().getAnnotation(annotationType);
		if (annotation == null) {
			annotation = factoryBean.getTypeDescriptor().getType().getAnnotation(annotationType);
		}
		return annotation;
	}
}
