package scw.beans.auto;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.annotation.AutoImpl;
import scw.beans.auto.annotation.Auto;
import scw.beans.auto.annotation.PropertyParameter;
import scw.beans.auto.annotation.ResourceParameter;
import scw.core.PropertyFactory;
import scw.core.annotation.NotRequire;
import scw.core.annotation.ParameterName;
import scw.core.annotation.ParameterValue;
import scw.core.exception.BeansException;
import scw.core.utils.AnnotationUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.CompareUtils;
import scw.core.utils.FormatUtils;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;

public final class AutoBeanUtils {
	private static final AutoBeanService DEFAULT_AUTO_BEAN_SERVICE = new DefaultAutoBeanService();
	private static final LinkedList<Object> services = new LinkedList<Object>();

	private AutoBeanUtils() {
	};

	private static Collection<AutoBeanService> getAutoBeanServices(AutoImpl autoConfig, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		LinkedList<AutoBeanService> autoBeanServices = new LinkedList<AutoBeanService>();
		String value = propertyFactory.getProperty("beans.auto.names");
		if (!StringUtils.isEmpty(value)) {
			value = FormatUtils.format(value, propertyFactory, true);
			String[] names = StringUtils.commonSplit(value);
			if (!ArrayUtils.isEmpty(names)) {
				for (String name : names) {
					if (StringUtils.isEmpty(name)) {
						continue;
					}

					autoBeanServices.add((AutoBeanService) beanFactory.getInstance(name));
				}
			}
		}

		for (Object service : services) {
			if (service == null) {
				continue;
			}

			if (service instanceof AutoBeanService) {
				autoBeanServices.add((AutoBeanService) service);
			} else {
				autoBeanServices.add((AutoBeanService) beanFactory.getInstance(service.toString()));
			}
		}

		if (autoConfig != null) {
			for (String name : autoConfig.serviceName()) {
				if (StringUtils.isEmpty(name)) {
					continue;
				}

				name = FormatUtils.format(name, propertyFactory, true);
				autoBeanServices.add((AutoBeanService) beanFactory.getInstance(name));
			}

			for (Class<? extends AutoBeanService> service : autoConfig.service()) {
				if (service == null) {
					continue;
				}

				autoBeanServices.add(beanFactory.getInstance(service));
			}
		}

		autoBeanServices.add(DEFAULT_AUTO_BEAN_SERVICE);
		return autoBeanServices;
	}

	public static void addAutoBeanService(String name) {
		synchronized (services) {
			services.add(name);
		}
	}

	public static void addAutoBeanService(AutoBeanService autoBeanService) {
		synchronized (services) {
			services.add(autoBeanService);
		}
	}

	public static AutoBean autoBeanService(Class<?> clazz, AutoImpl autoConfig, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		Collection<AutoBeanService> autoBeanServices = AutoBeanUtils.getAutoBeanServices(autoConfig, beanFactory,
				propertyFactory);
		if (!CollectionUtils.isEmpty(autoBeanServices)) {
			AutoBeanServiceChain serviceChain = new SimpleAutoBeanServiceChain(autoBeanServices);
			try {
				return serviceChain.service(clazz, beanFactory, propertyFactory);
			} catch (Exception e) {
				throw new BeansException(clazz.getName(), e);
			}
		}
		return null;
	}

	private static String getProperty(Class<?> clazz, ParameterName parameterName, ParameterValue parameterValue,
			PropertyFactory propertyFactory, String argName, ResourceParameter resourceParameter) {
		String value = propertyFactory.getProperty(
				parameterName == null ? (clazz.getClass().getName() + "." + argName) : parameterName.value());
		if (value == null) {
			if (parameterValue != null) {
				value = parameterValue.value();
			}
		}

		if (resourceParameter != null) {
			if (StringUtils.isEmpty(value)) {
				boolean b = StringUtils.isEmpty(resourceParameter.value()) ? false
						: ResourceUtils.isExist(resourceParameter.value());
				value = b ? resourceParameter.value() : null;
			} else {
				if (!ResourceUtils.isExist(value)) {
					boolean b = StringUtils.isEmpty(resourceParameter.value()) ? false
							: ResourceUtils.isExist(resourceParameter.value());
					value = b ? resourceParameter.value() : null;
				}
			}
		}
		return value;
	}

	private static boolean isProerptyType(PropertyParameter propertyParameter, Class<?> type) {
		if (propertyParameter == null) {
			return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class || type.isArray() || type.isEnum()
					|| Class.class == type || BigDecimal.class == type || BigInteger.class == type
					|| Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
		} else {
			return propertyParameter.value();
		}
	}

	public static Constructor<?> getAutoConstructor(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		LinkedList<Constructor<?>> autoList = new LinkedList<Constructor<?>>();
		LinkedList<Constructor<?>> defList = new LinkedList<Constructor<?>>();
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			Auto auto = constructor.getAnnotation(Auto.class);
			if (auto == null) {
				defList.add(constructor);
			} else {
				autoList.add(constructor);
			}
		}

		autoList.sort(new Comparator<Constructor<?>>() {

			public int compare(Constructor<?> o1, Constructor<?> o2) {
				Auto auto1 = o1.getAnnotation(Auto.class);
				Auto auto2 = o2.getAnnotation(Auto.class);
				return CompareUtils.compare(auto1 == null ? 0 : auto1.value(), auto2 == null ? 0 : auto2.value(), true);
			}
		});

		defList.sort(new Comparator<Constructor<?>>() {

			public int compare(Constructor<?> o1, Constructor<?> o2) {
				return CompareUtils.compare(o1.getParameterTypes().length, o2.getParameterTypes().length, true);
			}
		});

		autoList.addAll(defList);

		Class<?>[] parameterTypes = null;
		Constructor<?> constructor = null;
		for (int a = 0; a < autoList.size(); a++) {
			constructor = autoList.get(a);
			parameterTypes = constructor.getParameterTypes();
			if (parameterTypes.length == 0) {
				break;
			}

			Annotation[][] annotations = constructor.getParameterAnnotations();
			String[] names = ClassUtils.getParameterName(constructor);
			for (int i = 0; i < parameterTypes.length; i++) {
				ParameterName parameterName = AnnotationUtils.getAnnotation(annotations, ParameterName.class, i);
				NotRequire notRequire = AnnotationUtils.getAnnotation(annotations, NotRequire.class, i);
				boolean require = notRequire == null ? true : !notRequire.value();

				// 是否是属性而不是bean
				if (isProerptyType(AnnotationUtils.getAnnotation(annotations, PropertyParameter.class, i),
						parameterTypes[i])) {
					String value = getProperty(clazz, parameterName,
							AnnotationUtils.getAnnotation(annotations, ParameterValue.class, i), propertyFactory,
							names[i], AnnotationUtils.getAnnotation(annotations, ResourceParameter.class, i));

					if (require && StringUtils.isEmpty(value)) {
						parameterTypes = null;
						break;
					}
				} else {
					String name = parameterName == null ? parameterTypes[i].getName() : parameterName.value();
					if (StringUtils.isEmpty(name)) {
						name = parameterTypes[i].getName();
					}

					if (require && !beanFactory.isInstance(name)) {
						parameterTypes = null;
						break;
					}
				}
			}

			if (parameterTypes != null && parameterTypes.length != 0) {
				break;
			}
		}

		if (constructor == null) {
			return null;
		}

		if (parameterTypes == null) {
			return null;
		}

		return constructor;
	}

	public static Object[] getAutoArgs(Class<?> clazz, Constructor<?> constructor, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		if (parameterTypes.length == 0) {
			return new Object[0];
		}

		Type[] types = constructor.getGenericParameterTypes();
		Annotation[][] annotations = constructor.getParameterAnnotations();
		String[] names = ClassUtils.getParameterName(constructor);
		Object[] args = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			ParameterName parameterName = AnnotationUtils.getAnnotation(annotations, ParameterName.class, i);
			NotRequire notRequire = AnnotationUtils.getAnnotation(annotations, NotRequire.class, i);
			boolean require = notRequire == null ? true : !notRequire.value();

			if (isProerptyType(AnnotationUtils.getAnnotation(annotations, PropertyParameter.class, i),
					parameterTypes[i])) {
				String value = getProperty(clazz, parameterName,
						AnnotationUtils.getAnnotation(annotations, ParameterValue.class, i), propertyFactory, names[i],
						AnnotationUtils.getAnnotation(annotations, ResourceParameter.class, i));
				if (require && StringUtils.isEmpty(value)) {
					return null;
				}

				args[i] = StringParse.defaultParse(value, types[i]);
			} else {
				String name = parameterName == null ? parameterTypes[i].getName() : parameterName.value();
				if (StringUtils.isEmpty(name)) {
					name = parameterTypes[i].getName();
				}

				if (require && !beanFactory.isInstance(name)) {
					return null;
				}

				args[i] = beanFactory.isInstance(name) ? beanFactory.getInstance(name) : null;
			}
		}

		return args;
	}
}
