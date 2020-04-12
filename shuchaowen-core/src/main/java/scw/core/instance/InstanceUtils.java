package scw.core.instance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.core.annotation.AnnotationUtils;
import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.core.instance.annotation.Configuration;
import scw.core.instance.annotation.PropertyParameter;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.instance.support.ReflectionInstanceFactory;
import scw.core.instance.support.ReflectionSingleInstanceFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.CompareUtils;
import scw.core.utils.StringUtils;
import scw.io.resource.ResourceUtils;
import scw.lang.UnsupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.StringValue;
import scw.util.value.Value;
import scw.util.value.ValueUtils;
import scw.util.value.property.PropertyFactory;

public final class InstanceUtils {
	private static Logger logger = LoggerUtils
			.getConsoleLogger(InstanceUtils.class);

	private InstanceUtils() {
	};

	public static final ReflectionInstanceFactory REFLECTION_INSTANCE_FACTORY = new ReflectionInstanceFactory();

	public static final NoArgsInstanceFactory NO_ARGS_INSTANCE_FACTORY = getConfiguration(
			NoArgsInstanceFactory.class, REFLECTION_INSTANCE_FACTORY);

	public static final ReflectionSingleInstanceFactory SINGLE_INSTANCE_FACTORY = new ReflectionSingleInstanceFactory();

	/**
	 * 如果无参的构造方法调用失败就会使用不调用构造方法实例化
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> type) {
		T t = (T) REFLECTION_INSTANCE_FACTORY.getInstance(type);
		if (t == null) {
			t = (T) NO_ARGS_INSTANCE_FACTORY.getInstance(type);
		}

		if (t == null) {
			throw new UnsupportedException("无法实例化对象：" + type.getName());
		}
		return t;
	}

	/**
	 * 根据参数名来调用构造方法
	 * 
	 * @param type
	 * @param isPublic
	 * @param parameterMap
	 * @return
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> type, boolean isPublic,
			Map<String, Object> parameterMap) throws NoSuchMethodException {
		if (CollectionUtils.isEmpty(parameterMap)) {
			try {
				return ReflectionUtils.getConstructor(type, isPublic)
						.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}

		int size = parameterMap.size();
		for (Constructor<?> constructor : isPublic ? type.getConstructors()
				: type.getDeclaredConstructors()) {
			if (size == constructor.getParameterTypes().length) {
				String[] names = ParameterUtils.getParameterName(constructor);
				Object[] args = new Object[size];
				boolean find = true;
				for (int i = 0; i < names.length; i++) {
					if (!parameterMap.containsKey(names[i])) {
						find = false;
						break;
					}

					args[i] = parameterMap.get(names[i]);
				}

				if (find) {
					if (!Modifier.isPublic(constructor.getModifiers())) {
						constructor.setAccessible(true);
					}
					try {
						return (T) constructor.newInstance(args);
					} catch (Exception e) {
						new RuntimeException(e);
					}
					break;
				}
			}
		}

		throw new NoSuchMethodException(type.getName());
	}

	public static InstanceFactory getSingleInstanceFactory() {
		return SINGLE_INSTANCE_FACTORY;
	}

	public static <T> T getInstance(Class<? extends T> type,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		return getInstance(type, instanceFactory, propertyFactory,
				type.getName());
	}

	public static <T> T getInstance(Class<? extends T> type,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			String key) {
		String[] names = propertyFactory.getObject(key, String[].class);
		if (ArrayUtils.isEmpty(names)) {
			return null;
		}

		String name = names[0];
		if (!instanceFactory.isInstance(name)) {
			logger.warn("{} not create instance class {}", name, type);
			return null;
		}

		Object t = instanceFactory.getInstance(name);
		if (!ClassUtils.isAssignableValue(type, t)) {
			logger.warn("{} not is assignable from {}", name, type);
			return null;
		}
		return type.cast(t);
	}

	public static <T> Collection<T> getInstances(Class<? extends T> type,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		return getInstances(type, instanceFactory, propertyFactory,
				type.getName());
	}

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> getInstances(Class<? extends T> type,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			String key) {
		String[] names = propertyFactory.getObject(key, String[].class);
		if (ArrayUtils.isEmpty(names)) {
			return Collections.EMPTY_LIST;
		}

		List<T> list = new ArrayList<T>();
		for (String name : names) {
			if (!instanceFactory.isInstance(name)) {
				logger.warn("{} not create instance class {}", name, type);
				continue;
			}

			Object t = instanceFactory.getInstance(name);
			if (!ClassUtils.isAssignableValue(type, t)) {
				logger.warn("{} not is assignable from {}", name, type);
				continue;
			}

			list.add(type.cast(t));
		}
		return list.isEmpty() ? Collections.EMPTY_LIST : Collections
				.unmodifiableCollection(list);
	}

	private static boolean isProerptyType(ParameterDescriptor parameterConfig) {
		PropertyParameter propertyParameter = parameterConfig
				.getAnnotatedElement().getAnnotation(PropertyParameter.class);
		if (propertyParameter == null) {
			Class<?> type = parameterConfig.getType();
			return ValueUtils.isCommonType(type) || type.isArray()
					|| Collection.class.isAssignableFrom(type)
					|| Map.class.isAssignableFrom(type);
		} else {
			return propertyParameter.value();
		}
	}

	private static String getDefaultName(Class<?> clazz,
			ParameterDescriptor parameterConfig) {
		return clazz.getClass().getName() + "." + parameterConfig.getName();
	}

	private static Value getProperty(PropertyFactory propertyFactory,
			Class<?> clazz, ParameterDescriptor parameterConfig) {
		ParameterName parameterName = parameterConfig.getAnnotatedElement()
				.getAnnotation(ParameterName.class);
		Value value = propertyFactory
				.get(parameterName == null ? getDefaultName(clazz,
						parameterConfig) : parameterName.value());
		if (value == null) {
			DefaultValue defaultValue = parameterConfig.getAnnotatedElement()
					.getAnnotation(DefaultValue.class);
			if (defaultValue != null) {
				value = new StringValue(defaultValue.value());
			}
		}

		if (value != null) {
			ResourceParameter resourceParameter = parameterConfig
					.getAnnotatedElement().getAnnotation(
							ResourceParameter.class);
			if (resourceParameter != null) {
				if (!ResourceUtils.getResourceOperations().isExist(
						value.getAsString())) {
					return null;
				}
			}
		}
		return value;
	}

	private static String getInstanceName(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Class<?> clazz,
			ParameterDescriptor parameterConfig) {
		ParameterName parameterName = parameterConfig.getAnnotatedElement()
				.getAnnotation(ParameterName.class);
		if (parameterName != null
				&& StringUtils.isNotEmpty(parameterName.value())) {
			Value value = propertyFactory.get(parameterName.value());
			if (value == null) {
				return null;
			}

			return instanceFactory.isInstance(value.getAsString()) ? null
					: value.getAsString();
		} else {
			if (instanceFactory.isInstance(parameterConfig.getType())) {
				return parameterConfig.getType().getName();
			}

			String name = getDefaultName(clazz, parameterConfig);
			if (instanceFactory.isInstance(name)) {
				return name;
			}

			return null;
		}
	}

	public static boolean isAuto(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Class<?> clazz,
			ParameterDescriptor[] parameterConfigs, Object logFirstParameter) {
		if (parameterConfigs.length == 0) {
			return true;
		}

		for (int i = 0; i < parameterConfigs.length; i++) {
			ParameterDescriptor parameterConfig = parameterConfigs[i];
			boolean require = !AnnotationUtils.isNullable(
					parameterConfig.getAnnotatedElement(), false);
			if (!require) {
				continue;
			}

			boolean isProperty = isProerptyType(parameterConfig);
			// 是否是属性而不是bean
			boolean b = true;
			if (isProperty) {
				Value value = getProperty(propertyFactory, clazz,
						parameterConfig);
				if (value == null) {
					b = false;
				}
			} else {
				if (parameterConfig.getType() == InstanceFactory.class
						|| parameterConfig.getType() == PropertyFactory.class) {
					b = true;
				} else {
					String name = getInstanceName(instanceFactory,
							propertyFactory, clazz, parameterConfig);
					if (name == null) {
						b = false;
					}
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("{} parameter index {} is {} matching:{}",
						logFirstParameter, i, isProperty ? "property" : "bean",
						b ? "success" : "fail");
			}

			if (!b) {
				return false;
			}
		}
		return true;
	}

	public static Object[] getAutoArgs(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Class<?> clazz,
			ParameterDescriptor[] parameterConfigs) {
		if (parameterConfigs.length == 0) {
			return new Object[0];
		}

		Object[] args = new Object[parameterConfigs.length];
		for (int i = 0; i < parameterConfigs.length; i++) {
			ParameterDescriptor parameterConfig = parameterConfigs[i];
			boolean require = !AnnotationUtils.isNullable(
					parameterConfig.getAnnotatedElement(), false);
			if (isProerptyType(parameterConfig)) {
				Value value = getProperty(propertyFactory, clazz,
						parameterConfig);
				if (require && value == null) {
					return null;
				}

				args[i] = value.getAsObject(parameterConfig.getGenericType());
			} else {
				if (parameterConfig.getType() == InstanceConfig.class) {
					args[i] = instanceFactory;
					continue;
				}

				if (parameterConfig.getType() == PropertyFactory.class) {
					args[i] = propertyFactory;
					continue;
				}

				String name = getInstanceName(instanceFactory, propertyFactory,
						clazz, parameterConfig);
				args[i] = name == null ? null : instanceFactory
						.getInstance(name);
			}
		}
		return args;
	}

	private static Set<Class<?>> getConfigurationClassListInternal(
			Class<?> type, String packageName) {
		Set<Class<?>> list = new HashSet<Class<?>>();
		for (Class<?> clazz : ClassUtils.getClassSet(packageName)) {
			Configuration configuration = clazz
					.getAnnotation(Configuration.class);
			if (configuration == null) {
				continue;
			}

			if (!type.isAssignableFrom(clazz)) {
				continue;
			}

			if (!ClassUtils.isPresent(clazz.getName())) {
				logger.debug("not support class:{}", clazz.getName());
				continue;
			}

			list.add(clazz);
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	public static <T> List<Class<T>> getConfigurationClassList(
			Class<? extends T> type, Collection<Class> excludeTypes) {
		return getConfigurationClassList(type, excludeTypes, Arrays.asList(
				Constants.SYSTEM_PACKAGE_NAME, getScanAnnotationPackageName()));
	}

	@SuppressWarnings("rawtypes")
	public static <T> List<Class<T>> getConfigurationClassList(
			Class<? extends T> type, Class... excludeTypes) {
		return getConfigurationClassList(type, Arrays.asList(excludeTypes));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<Class<T>> getConfigurationClassList(
			Class<? extends T> type, Collection<Class> excludeTypes,
			Collection<String> packageNames) {
		HashSet<Class<T>> set = new HashSet<Class<T>>();
		for (String packageName : packageNames) {
			for (Class<?> clazz : getConfigurationClassListInternal(type,
					packageName)) {
				Configuration configuration = clazz
						.getAnnotation(Configuration.class);
				if (configuration == null) {
					continue;
				}

				if (!CollectionUtils.isEmpty(excludeTypes)) {
					for (Class<?> excludeType : excludeTypes) {
						if (excludeType.isAssignableFrom(clazz)) {
							continue;
						}
					}
				}
				set.add((Class<T>) clazz);
			}
		}

		List<Class<T>> list = new ArrayList<Class<T>>(set);
		for (Class<? extends T> clazz : list) {
			Configuration c = clazz.getAnnotation(Configuration.class);
			for (Class<?> e : c.excludes()) {
				if (e == clazz) {
					continue;
				}
				set.remove(e);
			}
		}

		list = new ArrayList<Class<T>>(set);
		Comparator<Class<? extends T>> comparator = new Comparator<Class<? extends T>>() {

			public int compare(Class<? extends T> o1, Class<? extends T> o2) {
				Configuration c1 = o1.getAnnotation(Configuration.class);
				Configuration c2 = o2.getAnnotation(Configuration.class);
				return CompareUtils.compare(c1.order(), c2.order(), true);
			}
		};
		Collections.sort(list, comparator);
		return list;
	}

	@SuppressWarnings("rawtypes")
	public static <T> List<T> getConfigurationList(Class<? extends T> type,
			InstanceFactory instanceFactory, Collection<Class> excludeTypes) {
		return getConfigurationList(type, instanceFactory, excludeTypes,
				Arrays.asList(Constants.SYSTEM_PACKAGE_NAME,
						getScanAnnotationPackageName()));
	}

	@SuppressWarnings("rawtypes")
	public static <T> List<T> getConfigurationList(Class<? extends T> type,
			InstanceFactory instanceFactory, Collection<Class> excludeTypes,
			Collection<String> packageNames) {
		List<T> list = new ArrayList<T>();
		for (Class<T> clazz : getConfigurationClassList(type, excludeTypes,
				packageNames)) {
			if (!instanceFactory.isInstance(clazz)) {
				logger.debug("factory [{}] not create instance:{}",
						instanceFactory.getClass(), clazz);
				continue;
			}

			list.add(instanceFactory.getInstance(clazz));
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	public static <T> List<T> getConfigurationList(Class<? extends T> type,
			InstanceFactory instanceFactory, Class... excludeTypes) {
		return getConfigurationList(type, instanceFactory,
				Arrays.asList(excludeTypes));
	}

	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue(
				"scw.scan.annotation.package", String.class,
				GlobalPropertyFactory.getInstance().getBasePackageName());
	}

	@SuppressWarnings("rawtypes")
	public static <T> T getConfiguration(Class<? extends T> type,
			InstanceFactory instanceFactory, Collection<Class> excludeTypes,
			Collection<String> packageNames) {
		for (Class<T> clazz : getConfigurationClassList(type, excludeTypes,
				packageNames)) {
			if (!instanceFactory.isInstance(clazz)) {
				logger.debug("factory [{}] not create instance:{}",
						instanceFactory.getClass(), clazz);
				continue;
			}

			return instanceFactory.getInstance(clazz);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static <T> T getConfiguration(Class<? extends T> type,
			InstanceFactory instanceFactory, Collection<Class> excludeTypes) {
		return getConfiguration(type, instanceFactory, excludeTypes,
				Arrays.asList(Constants.SYSTEM_PACKAGE_NAME,
						getScanAnnotationPackageName()));
	}

	@SuppressWarnings("rawtypes")
	public static <T> T getConfiguration(Class<? extends T> type,
			InstanceFactory instanceFactory, Class... excludeTypes) {
		return getConfiguration(type, instanceFactory,
				Arrays.asList(excludeTypes));
	}

	public static <T> T getConfiguration(Class<? extends T> type,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		T t = getInstance(type, instanceFactory, propertyFactory);
		if (t == null) {
			t = getConfiguration(type, instanceFactory);
		}
		return t;
	}

	public static <T> List<T> getConfigurationList(Class<? extends T> type,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		List<T> list = new ArrayList<T>();
		list.addAll(getConfigurationList(type, instanceFactory));
		list.addAll(getInstances(type, instanceFactory, propertyFactory));
		return Collections.unmodifiableList(list);
	}
}
