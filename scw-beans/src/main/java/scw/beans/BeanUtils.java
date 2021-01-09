package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.annotation.AopEnable;
import scw.beans.annotation.Service;
import scw.beans.annotation.Singleton;
import scw.core.Constants;
import scw.core.OrderComparator;
import scw.core.Ordered;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.InstanceUtils;
import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.util.MultiServiceLoader;
import scw.util.ServiceLoader;
import scw.value.ValueFactory;
import scw.value.property.PropertyFactory;

public final class BeanUtils {
	private static final List<AopEnableSpi> AOP_ENABLE_SPIS = InstanceUtils
			.loadAllService(AopEnableSpi.class);

	private BeanUtils() {
	};

	public static boolean isSingleton(Class<?> type,
			AnnotatedElement annotatedElement) {
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

	public static String getScanAnnotationPackageName(
			ValueFactory<String> propertyFactory) {
		return propertyFactory.getValue("scw.scan.beans.package", String.class,
				InstanceUtils.getScanAnnotationPackageName(propertyFactory));
	}

	public static Class<?> getServiceInterface(Class<?> clazz) {
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (Class<?> i : classToUse.getInterfaces()) {
				if (AnnotationUtils.isIgnore(classToUse)
						|| i.getMethods().length == 0) {
					continue;
				}

				return i;
			}
			classToUse = classToUse.getSuperclass();
		}
		return null;
	}

	public static void init(Object init) throws Throwable {
		if (init == null) {
			return;
		}

		if (init instanceof Init) {
			((Init) init).init();
		}
	}

	public static void destroy(Object destroy) throws Throwable {
		if (destroy == null) {
			return;
		}

		if (destroy instanceof Destroy) {
			((Destroy) destroy).destroy();
		}
	}

	public static void aware(Object instance, BeanFactory beanFactory,
			BeanDefinition beanDefinition) {
		if (instance instanceof BeanFactoryAware) {
			((BeanFactoryAware) instance).setBeanFactory(beanFactory);
		}

		if (instance instanceof BeanDefinitionAware) {
			((BeanDefinitionAware) instance).setBeanDefinition(beanDefinition);
		}
	}

	/**
	 * 此结果已排序
	 * @see Ordered
	 * @param clazz
	 * @param beanFactory
	 * @param propertyFactory
	 * @return
	 */
	public static <T> List<T> loadAllService(Class<? extends T> clazz,
			BeanFactory beanFactory, PropertyFactory propertyFactory) {
		List<T> services = Collections.list(CollectionUtils.toEnumeration(getServiceLoader(
				clazz, beanFactory, propertyFactory).iterator()));
		if(CollectionUtils.isEmpty(services)){
			return services;
		}
		
		Collections.sort(services, OrderComparator.INSTANCE);
		return services;
	}

	@SuppressWarnings("unchecked")
	public static <T> ServiceLoader<T> getServiceLoader(
			Class<? extends T> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		return new MultiServiceLoader<T>(InstanceUtils.getServiceLoader(clazz,
				beanFactory, propertyFactory),
				InstanceUtils.getConfigurationServiceLoader(clazz, beanFactory,
						null, Arrays.asList(Constants.SYSTEM_PACKAGE_NAME,
								getScanAnnotationPackageName(propertyFactory))));
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

	public static void destroy(BeanFactory beanFactory,
			Map<String, Object> instanceMap, Logger logger) {
		List<String> beanKeyList = new ArrayList<String>();
		for (Entry<String, Object> entry : instanceMap.entrySet()) {
			beanKeyList.add(entry.getKey());
		}

		ListIterator<String> keyIterator = beanKeyList.listIterator(beanKeyList
				.size());
		while (keyIterator.hasPrevious()) {
			BeanDefinition beanDefinition = beanFactory
					.getDefinition(keyIterator.previous());
			if (beanDefinition == null) {
				continue;
			}

			Object obj = instanceMap.get(beanDefinition.getId());
			try {
				beanDefinition.destroy(obj);
			} catch (Throwable e) {
				logger.error(e, "destroy error: {}", beanDefinition.getId());
			}
		}
	}

	/**
	 * 默认是不使用代理的，除非使用以下方式(see)：
	 * @see AopEnable
	 * @see Service
	 * @see AopEnableSpi
	 * @param clazz
	 * @param annotatedElement
	 * @return
	 */
	public static boolean isAopEnable(Class<?> clazz,
			AnnotatedElement annotatedElement) {
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
