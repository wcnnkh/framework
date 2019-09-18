package scw.beans.auto;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.beans.annotation.AutoConfig;
import scw.beans.auto.annotation.Auto;
import scw.beans.auto.annotation.PropertyParameter;
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
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XUtils;

public final class AutoBeanUtils {
	private static final AutoBeanService DEFAULT_AUTO_BEAN_SERVICE = new DefaultAutoBeanService();
	private static final LinkedList<Object> services = new LinkedList<Object>();

	private AutoBeanUtils() {
	};

	public static Collection<AutoBeanService> getAutoBeanServices(AutoConfig autoConfig, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		LinkedList<AutoBeanService> autoBeanServices = new LinkedList<AutoBeanService>();
		String value = SystemPropertyUtils.getProperty("beans.auto.names");
		if (!StringUtils.isEmpty(value)) {
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
			for (String name : autoConfig.autoBeanServiceNames()) {
				if (StringUtils.isEmpty(name)) {
					continue;
				}

				autoBeanServices.add((AutoBeanService) beanFactory.getInstance(name));
			}

			for (Class<? extends AutoBeanService> service : autoConfig.autoBeanServices()) {
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

	public static AutoBean autoBeanService(Class<?> clazz, AutoConfig autoConfig, BeanFactory beanFactory,
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

	public static Constructor<?> getAutoConstructor(Class<?> type, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		LinkedList<Constructor<?>> autoList = new LinkedList<Constructor<?>>();
		LinkedList<Constructor<?>> defList = new LinkedList<Constructor<?>>();
		for (Constructor<?> constructor : type.getDeclaredConstructors()) {
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
		String packageName = type.getPackage().getName();
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
				PropertyParameter propertyParameter = AnnotationUtils.getAnnotation(annotations,
						PropertyParameter.class, i);
				NotRequire notRequire = AnnotationUtils.getAnnotation(annotations, NotRequire.class, i);
				boolean require = notRequire == null ? true : !notRequire.value();

				Class<?> parameterType = parameterTypes[i];
				// 是否是属性而不是bean
				boolean isProperty;
				if (propertyParameter == null) {
					isProperty = XUtils.isCommonType(parameterType);
				} else {
					isProperty = propertyParameter.value();
				}

				if (isProperty) {
					String value = propertyFactory.getProperty(parameterName == null
							? (StringUtils.isEmpty(packageName) ? names[i] : (packageName + "." + names[i]))
							: parameterName.value());
					if (value == null) {
						ParameterValue parameterValue = AnnotationUtils.getAnnotation(annotations, ParameterValue.class,
								i);
						if (parameterValue != null) {
							value = parameterValue.value();
						}
					}

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

	public static Object[] getAutoArgs(Constructor<?> constructor, BeanFactory beanFactory,
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
			PropertyParameter propertyParameter = AnnotationUtils.getAnnotation(annotations, PropertyParameter.class,
					i);
			NotRequire notRequire = AnnotationUtils.getAnnotation(annotations, NotRequire.class, i);
			boolean require = notRequire == null ? true : !notRequire.value();

			Class<?> parameterType = parameterTypes[i];
			// 是否是属性而不是bean
			boolean isProperty;
			if (propertyParameter == null) {
				isProperty = ClassUtils.isPrimitiveOrWrapper(parameterType) || parameterType == String.class
						|| parameterType.isArray();
			} else {
				isProperty = propertyParameter.value();
			}

			if (isProperty) {
				String value = propertyFactory.getProperty(parameterName == null ? names[i] : parameterName.value());
				if (value == null) {
					ParameterValue parameterValue = AnnotationUtils.getAnnotation(annotations, ParameterValue.class, i);
					if (parameterValue != null) {
						value = parameterValue.value();
					}
				}

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
