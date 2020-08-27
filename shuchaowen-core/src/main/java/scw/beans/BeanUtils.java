package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.List;

import scw.aop.MethodInterceptor;
import scw.beans.annotation.AopEnable;
import scw.beans.annotation.Bean;
import scw.beans.annotation.Service;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.core.Constants;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;
import scw.value.ValueFactory;

public final class BeanUtils {
	private static final List<String> DISABLE_PROXY_BEANS = ResourceUtils.getLines(
			ResourceUtils.getResourceOperations().getResource("/scw/beans/disable-proxy.beans"),
			Constants.DEFAULT_CHARSET);

	private BeanUtils() {
	};

	public static boolean isSingletion(Class<?> type, AnnotatedElement annotatedElement) {
		Bean bean = annotatedElement.getAnnotation(Bean.class);
		return bean == null ? true : bean.singleton();
	}

	public static boolean isAopEnable(Class<?> type, AnnotatedElement annotatedElement) {
		if (Modifier.isFinal(type.getModifiers())) {// final修饰的类无法代理
			return false;
		}

		if (type.getName().startsWith("java.") || type.getName().startsWith("javax.")) {
			return false;
		}

		if (MethodInterceptor.class.isAssignableFrom(type) || BeanConfiguration.class.isAssignableFrom(type)
				|| BeanBuilderLoader.class.isAssignableFrom(type) || BeanBuilderLoaderChain.class.isAssignableFrom(type)
				|| BeanDefinition.class.isAssignableFrom(type)) {
			return false;
		}

		for (String name : DISABLE_PROXY_BEANS) {
			if (StringUtils.test(type.getName(), name)) {
				return false;
			}
		}

		AopEnable aopEnable = annotatedElement.getAnnotation(AopEnable.class);
		if (aopEnable != null) {
			return aopEnable.value();
		}

		// 如果是一个服务那么应该默认使用aop
		Service service = annotatedElement.getAnnotation(Service.class);
		if (service != null) {
			return true;
		}

		Class<?> useClass = type;
		while (useClass != null && useClass != Object.class) {
			aopEnable = useClass.getAnnotation(AopEnable.class);
			if (aopEnable != null) {
				return aopEnable.value();
			}

			for (Class<?> interfaceClass : useClass.getInterfaces()) {
				aopEnable = interfaceClass.getAnnotation(AopEnable.class);
				if (aopEnable != null) {
					return aopEnable.value();
				}
			}
			useClass = useClass.getSuperclass();
		}
		return true;
	}

	public static String getScanAnnotationPackageName(ValueFactory<String> propertyFactory) {
		return propertyFactory.getValue("scw.scan.beans.package", String.class,
				InstanceUtils.getScanAnnotationPackageName(propertyFactory));
	}

	public static Class<?> getServiceInterface(Class<?> clazz) {
		for (Class<?> i : clazz.getInterfaces()) {
			if (AnnotationUtils.isIgnore(clazz) || i.getMethods().length == 0) {
				continue;
			}

			return i;
		}
		return null;
	}

	public static void init(Object init) throws Exception {
		if (init == null) {
			return;
		}

		if (init instanceof Init) {
			((Init) init).init();
		}
	}

	public static void destroy(Object destroy) throws Exception {
		if (destroy == null) {
			return;
		}

		if (destroy instanceof Destroy) {
			((Destroy) destroy).destroy();
		}
	}

	public static void aware(Object instance, BeanFactory beanFactory, BeanDefinition beanDefinition) {
		if (instance instanceof BeanFactoryAware) {
			((BeanFactoryAware) instance).setBeanFactory(beanFactory);
		}

		if (instance instanceof BeanDefinitionAware) {
			((BeanDefinitionAware) instance).setBeanDefinition(beanDefinition);
		}
	}
}
