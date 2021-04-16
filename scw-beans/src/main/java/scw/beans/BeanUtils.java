package scw.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.List;

import scw.beans.annotation.AopEnable;
import scw.beans.annotation.IgnoreConfigurationProperty;
import scw.beans.annotation.Service;
import scw.beans.annotation.Singleton;
import scw.convert.support.EntityConversionService;
import scw.convert.support.PropertyFactoryToEntityConversionService;
import scw.core.annotation.AnnotationUtils;
import scw.env.Environment;
import scw.instance.InstanceUtils;
import scw.mapper.Field;
import scw.util.Accept;

public final class BeanUtils {
	private static final List<AopEnableSpi> AOP_ENABLE_SPIS = InstanceUtils.loadAllService(AopEnableSpi.class);

	private BeanUtils() {
	};

	public static boolean isSingleton(Class<?> type, AnnotatedElement annotatedElement) {
		Singleton singleton = annotatedElement.getAnnotation(Singleton.class);
		if (singleton != null) {
			return singleton.value();
		}

		for (Class<?> interfaceClass : type.getInterfaces()) {
			if (!isSingleton(interfaceClass, annotatedElement)) {
				return false;
			}
		}
		// 默认是单例
		return true;
	}

	public static Class<?> getServiceInterface(Class<?> clazz) {
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (Class<?> i : classToUse.getInterfaces()) {
				if (AnnotationUtils.isIgnore(classToUse) || i.getMethods().length == 0) {
					continue;
				}

				return i;
			}
			classToUse = classToUse.getSuperclass();
		}
		return null;
	}

	public static void aware(Object instance, BeanFactory beanFactory, BeanDefinition beanDefinition) {
		if (instance instanceof BeanFactoryAware) {
			((BeanFactoryAware) instance).setBeanFactory(beanFactory);
		}

		if (instance instanceof BeanDefinitionAware) {
			((BeanDefinitionAware) instance).setBeanDefinition(beanDefinition);
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

	public static EntityConversionService createEntityConversionService(Environment environment) {
		PropertyFactoryToEntityConversionService entityConversionService = new PropertyFactoryToEntityConversionService(
				environment);
		entityConversionService.setStrict(false);
		entityConversionService.getFieldAccept().add(new Accept<Field>() {

			public boolean accept(Field field) {
				IgnoreConfigurationProperty ignore = field.getAnnotatedElement()
						.getAnnotation(IgnoreConfigurationProperty.class);
				if (ignore != null) {
					return false;
				}

				// 如果字段上存在beans下的注解应该忽略此字段
				for (Annotation annotation : field.getAnnotatedElement().getAnnotations()) {
					if (annotation.annotationType().getName().startsWith("scw.beans.")) {
						return false;
					}
				}
				return true;
			}
		});
		return entityConversionService;
	}
}
