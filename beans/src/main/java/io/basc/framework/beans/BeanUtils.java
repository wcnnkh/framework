package io.basc.framework.beans;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.List;

import io.basc.framework.beans.annotation.AopEnable;
import io.basc.framework.beans.annotation.Service;
import io.basc.framework.context.ContextAware;
import io.basc.framework.env.EnvironmentAware;
import io.basc.framework.env.Sys;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.support.DefaultValueFactoryAware;
import io.basc.framework.lang.Ignore;
import io.basc.framework.util.ClassUtils;

public final class BeanUtils {
	private static final List<AopEnableSpi> AOP_ENABLE_SPIS = Sys.env.getServiceLoader(AopEnableSpi.class).toList();

	private BeanUtils() {
	};

	public static Class<?> getServiceInterface(Class<?> clazz) {
		return ClassUtils.getInterfaces(clazz).streamAll().filter((i) -> {
			if (i.isAnnotationPresent(Ignore.class) || i.getMethods().length == 0) {
				return false;
			}
			return true;
		}).findFirst().orElse(null);
	}

	public static void aware(Object instance, BeanFactory beanFactory, BeanDefinition beanDefinition) {
		if (instance instanceof BeanFactoryAware) {
			((BeanFactoryAware) instance).setBeanFactory(beanFactory);
		}

		if (instance instanceof BeanDefinitionAware) {
			((BeanDefinitionAware) instance).setBeanDefinition(beanDefinition);
		}

		if (instance instanceof EnvironmentAware) {
			((EnvironmentAware) instance).setEnvironment(beanFactory.getEnvironment());
		}

		if (instance instanceof ContextAware) {
			((ContextAware) instance).setContext(beanFactory);
		}

		if (instance instanceof Configurable) {
			((Configurable) instance).configure(beanFactory);
		}

		if (instance instanceof DefaultValueFactoryAware) {
			((DefaultValueFactoryAware) instance).setDefaultValueFactory(beanFactory.getDefaultValueFactory());
		}
	}

	public static RuntimeBean getRuntimeBean(Object instance) {
		if (instance == null) {
			return null;
		}

		if (instance instanceof RuntimeBean) {
			return ((RuntimeBean) instance);
		}

		return null;
	}

	/**
	 * 默认是不使用代理的，除非使用以下方式(see)：
	 * 
	 * @see AopEnable
	 * @see Service
	 * @see AopEnableSpi
	 * @param clazz
	 * @param annotatedElement
	 * @return
	 */
	public static boolean isAopEnable(Class<?> clazz, AnnotatedElement annotatedElement) {
		if (Modifier.isFinal(clazz.getModifiers())) {// final修饰的类无法代理
			return false;
		}

		AopEnable aopEnable = annotatedElement.getAnnotation(AopEnable.class);
		if (aopEnable != null) {
			return aopEnable.value();
		}

		aopEnable = clazz.getAnnotation(AopEnable.class);
		if (aopEnable != null) {
			return aopEnable.value();
		}

		// 如果是一个服务那么应该默认使用aop
		Service service = clazz.getAnnotation(Service.class);
		if (service != null) {
			return true;
		}

		for (AopEnableSpi spi : AOP_ENABLE_SPIS) {
			if (spi.isAopEnable(clazz, annotatedElement)) {
				return true;
			}
		}

		Class<?> classToUse = clazz.getSuperclass();
		while (classToUse != null && classToUse != Object.class) {
			if (isAopEnable(classToUse, classToUse)) {
				return true;
			}

			for (Class<?> interfaceClass : classToUse.getInterfaces()) {
				if (isAopEnable(interfaceClass, interfaceClass)) {
					return true;
				}
			}
			classToUse = classToUse.getSuperclass();
		}
		return false;
	}
}
