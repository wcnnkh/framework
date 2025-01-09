package io.basc.framework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.beans.BeansException;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.collection.ServiceLoader;
import lombok.NonNull;

public interface ListableBeanFactory extends BeanFactory {
	Elements<String> getBeanNames();

	default Elements<String> getBeanNamesForType(@NonNull ResolvableType requiredType) {
		return getBeanNames().filter((name) -> isTypeMatch(name, requiredType));
	}

	default Elements<String> getBeanNamesForType(@NonNull Class<?> requiredType) {
		return getBeanNames().filter((name) -> isTypeMatch(name, requiredType));
	}

	Elements<String> getFactoryBeanNames();

	@SuppressWarnings("unchecked")
	@Override
	default <T> T getBean(@NonNull Class<T> requiredType) throws BeansException {
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
	default <S> ServiceLoader<S> getServiceLoader(@NonNull ResolvableType requiredType) {
		Elements<String> names = getBeanNamesForType(requiredType);
		return new NameBeanProvider<>(names, this);
	}

	@Override
	default Object getBean(@NonNull ResolvableType requiredType) throws BeansException {
		Elements<String> beanNames = getBeanNamesForType(requiredType).toList();
		if (beanNames.isEmpty()) {
			throw new NoSuchBeanDefinitionException(requiredType);
		}

		if (!beanNames.isUnique()) {
			throw new NoUniqueBeanDefinitionException(requiredType, beanNames.toList());
		}

		return getBean(beanNames.first());
	}

	default Map<String, Object> getBeansOfType(@NonNull ResolvableType requiredType) throws BeansException {
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
	default <T> Map<String, T> getBeansOfType(@NonNull Class<T> requiredType) throws BeansException {
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

	default Elements<String> getBeanNamesForAnnotation(@NonNull Class<? extends Annotation> annotationType) {
		return getBeanNames().filter((name) -> findAnnotationOnBean(name, annotationType) != null);
	}

	default Map<String, Object> getBeansWithAnnotation(@NonNull Class<? extends Annotation> annotationType)
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

	default <A extends Annotation> A findAnnotationOnBean(@NonNull String beanName, @NonNull Class<A> annotationType)
			throws NoSuchBeanDefinitionException {
		FactoryBean<?> factoryBean = getFactoryBean(beanName);
		A annotation = factoryBean.getTypeDescriptor().getAnnotation(annotationType);
		if (annotation == null) {
			annotation = factoryBean.getTypeDescriptor().getType().getAnnotation(annotationType);
		}
		return annotation;
	}
}
