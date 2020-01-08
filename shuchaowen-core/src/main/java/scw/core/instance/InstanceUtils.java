package scw.core.instance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import scw.core.PropertyFactory;
import scw.core.SystemPropertyFactory;
import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.core.instance.annotation.PropertyParameter;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.instance.support.ReflectionInstanceFactory;
import scw.core.instance.support.ReflectionSingleInstanceFactory;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.AnnotationUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.resource.ResourceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.lang.NotSupportException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.FormatUtils;
import scw.util.Value;
import scw.util.ValueFactory;

public final class InstanceUtils {
	private static Logger logger = LoggerUtils.getLogger(InstanceUtils.class);

	private InstanceUtils() {
	};

	public static final ReflectionInstanceFactory REFLECTION_INSTANCE_FACTORY = new ReflectionInstanceFactory();

	public static final NoArgsInstanceFactory NO_ARGS_INSTANCE_FACTORY;

	public static final ReflectionSingleInstanceFactory SINGLE_INSTANCE_FACTORY = new ReflectionSingleInstanceFactory();

	static {
		NoArgsInstanceFactory instanceFactory = REFLECTION_INSTANCE_FACTORY
				.getInstance("scw.core.instance.support.SunNoArgsInstanceFactory");
		if (instanceFactory == null) {
			instanceFactory = REFLECTION_INSTANCE_FACTORY
					.getInstance("scw.core.instance.support.UnsafeNoArgsInstanceFactory");
		}

		if (instanceFactory == null) {
			throw new NotSupportException("Instances that do not call constructors are not supported");
		}

		NO_ARGS_INSTANCE_FACTORY = instanceFactory;
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String name, boolean invokeConstructor) {
		return (T) (invokeConstructor ? REFLECTION_INSTANCE_FACTORY.getInstance(name)
				: NO_ARGS_INSTANCE_FACTORY.getInstance(name));
	}

	public static <T> T newInstance(Class<T> type, boolean invokeConstructor) {
		return (T) (invokeConstructor ? REFLECTION_INSTANCE_FACTORY.getInstance(type)
				: NO_ARGS_INSTANCE_FACTORY.getInstance(type));
	}

	/**
	 * 如果无参的构造方法调用失败就会使用不调用构造方法实例化
	 * 
	 * @param name
	 * @return
	 */
	public static <T> T newInstance(String name) {
		T t = REFLECTION_INSTANCE_FACTORY.getInstance(name);
		if (t == null) {
			t = NO_ARGS_INSTANCE_FACTORY.getInstance(name);
		}

		if (t == null) {
			throw new NotSupportException("无法实例化对象：" + name);
		}
		return t;
	}

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
			throw new NotSupportException("无法实例化对象：" + type.getName());
		}
		return t;
	}

	/**
	 * 执行失败返回空
	 * 
	 * @param name
	 * @param params
	 * @return
	 */
	public static <T> T getInstance(String name, Object... params) {
		return REFLECTION_INSTANCE_FACTORY.getInstance(name, params);
	}

	/**
	 * 执行失败返回空
	 * 
	 * @param type
	 * @param params
	 * @return
	 */
	public static <T> T getInstance(Class<T> type, Object... params) {
		return REFLECTION_INSTANCE_FACTORY.getInstance(type, params);
	}

	/**
	 * 执行失败返回空
	 * 
	 * @param name
	 * @param parameterTypes
	 * @param params
	 * @return
	 */
	public static <T> T getInstance(String name, Class<?>[] parameterTypes, Object... params) {
		return REFLECTION_INSTANCE_FACTORY.getInstance(name, parameterTypes, params);
	}

	/**
	 * 执行失败返回空或抛出异常
	 * 
	 * @param type
	 * @param parameterTypes
	 * @param params
	 * @return
	 */
	public static <T> T getInstance(Class<T> type, Class<?>[] parameterTypes, Object... params) {
		return REFLECTION_INSTANCE_FACTORY.getInstance(type, parameterTypes, params);
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
	public static <T> T newInstance(Class<T> type, boolean isPublic, Map<String, Object> parameterMap)
			throws NoSuchMethodException {
		if (CollectionUtils.isEmpty(parameterMap)) {
			try {
				return ReflectionUtils.getConstructor(type, isPublic).newInstance();
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
		for (Constructor<?> constructor : isPublic ? type.getConstructors() : type.getDeclaredConstructors()) {
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

	@SuppressWarnings("unchecked")
	public static <T> T autoNewInstance(Class<T> clazz, InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) throws Exception {
		InstanceConfig instanceConfig = new AutoInstanceConfig(instanceFactory, propertyFactory, clazz);
		if (instanceConfig.getConstructor() == null) {
			return null;
		}

		return (T) instanceConfig.getConstructor().newInstance(instanceConfig.getArgs());
	}

	@SuppressWarnings("unchecked")
	public static <T> T autoNewInstance(String name) throws Exception {
		return (T) autoNewInstance(ClassUtils.forName(name, ClassUtils.getDefaultClassLoader()));
	}

	public static <T> T autoNewInstance(Class<T> clazz, InstanceFactory instanceFactory) throws Exception {
		return autoNewInstance(clazz, instanceFactory, SystemPropertyFactory.INSTANCE);
	}

	public static <T> T autoNewInstance(Class<T> clazz) throws Exception {
		return autoNewInstance(clazz, REFLECTION_INSTANCE_FACTORY);
	}

	public static <T> T autoNewInstanceBySystemProperty(Class<T> clazz, String key, T defaultValue) {
		String name = SystemPropertyUtils.getProperty(key);
		if (StringUtils.isEmpty(name)) {
			return defaultValue;
		}
		Class<?> clz;
		try {
			clz = ClassUtils.forName(name, ClassUtils.getDefaultClassLoader());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return defaultValue;
		}

		if (clz.isAssignableFrom(clazz)) {
			FormatUtils.warn(InstanceUtils.class, "{} not is assignable from {}", clz, clazz);
			return defaultValue;
		}

		Object bean;
		try {
			bean = autoNewInstance(clz);
		} catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}

		return clazz.cast(bean);
	}

	public static <T> Collection<? extends T> autoNewInstancesBySystemProperty(Class<T> clazz, String key,
			Collection<? extends T> defaultValues) {
		String names = SystemPropertyUtils.getProperty(key);
		if (StringUtils.isEmpty(names)) {
			return defaultValues;
		}

		LinkedList<T> list = new LinkedList<T>();
		for (String name : StringUtils.commonSplit(names)) {
			Class<?> clz;
			try {
				clz = ClassUtils.forName(name, ClassUtils.getDefaultClassLoader());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			}

			if (clz.isAssignableFrom(clazz)) {
				FormatUtils.warn(InstanceUtils.class, "{} not is assignable from {}", clz, clazz);
				continue;
			}

			Object bean;
			try {
				bean = autoNewInstance(clz);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			list.add(clazz.cast(bean));
		}
		return list.isEmpty() ? defaultValues : list;
	}

	private static boolean isProerptyType(ParameterConfig parameterConfig) {
		PropertyParameter propertyParameter = parameterConfig.getAnnotation(PropertyParameter.class);
		if (propertyParameter == null) {
			Class<?> type = parameterConfig.getType();
			return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class || type.isArray() || type.isEnum()
					|| Class.class == type || BigDecimal.class == type || BigInteger.class == type
					|| Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
		} else {
			return propertyParameter.value();
		}
	}

	private static String getDefaultName(Class<?> clazz, ParameterConfig parameterConfig) {
		return clazz.getClass().getName() + "." + parameterConfig.getName();
	}

	private static String getProperty(PropertyFactory propertyFactory,
			ValueFactory<String, ? extends Value> valueFactory, Class<?> clazz, ParameterConfig parameterConfig) {
		ParameterName parameterName = parameterConfig.getAnnotation(ParameterName.class);
		String value = propertyFactory
				.getProperty(parameterName == null ? getDefaultName(clazz, parameterConfig) : parameterName.value());
		if (value == null) {
			DefaultValue defaultValue = parameterConfig.getAnnotation(DefaultValue.class);
			if (defaultValue != null) {
				value = defaultValue.value();
			}
		}

		if (value != null) {
			ResourceParameter resourceParameter = parameterConfig.getAnnotation(ResourceParameter.class);
			if (resourceParameter != null) {
				if (!ResourceUtils.getResourceOperations().isExist(value)) {
					return null;
				}
			}
		}
		return value;
	}

	private static String getInstanceName(InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class<?> clazz, ParameterConfig parameterConfig) {
		ParameterName parameterName = parameterConfig.getAnnotation(ParameterName.class);
		if (parameterName != null && StringUtils.isNotEmpty(parameterName.value())) {
			String value = propertyFactory.getProperty(parameterName.value());
			if (value == null) {
				return null;
			}

			return instanceFactory.isInstance(value) ? null : value;
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

	public static boolean isAuto(InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			ValueFactory<String, ? extends Value> valueFactory, Class<?> clazz, ParameterConfig[] parameterConfigs,
			Object logFirstParameter) {
		if (parameterConfigs.length == 0) {
			return true;
		}

		for (int i = 0; i < parameterConfigs.length; i++) {
			ParameterConfig parameterConfig = parameterConfigs[i];
			boolean require = !AnnotationUtils.isNullable(parameterConfig, false);
			if (!require) {
				continue;
			}

			boolean isProperty = isProerptyType(parameterConfig);
			// 是否是属性而不是bean
			boolean b = true;
			if (isProperty) {
				String value = getProperty(propertyFactory, valueFactory, clazz, parameterConfig);
				if (StringUtils.isEmpty(value)) {
					b = false;
				}
			} else {
				if (parameterConfig.getType() == InstanceFactory.class
						|| parameterConfig.getType() == PropertyFactory.class) {
					b = true;
				} else {
					String name = getInstanceName(instanceFactory, propertyFactory, clazz, parameterConfig);
					if (name == null) {
						b = false;
					}
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("{} parameter index {} is {} matching:{}", logFirstParameter, i,
						isProperty ? "property" : "bean", b ? "success" : "fail");
			}

			if (!b) {
				return false;
			}
		}
		return true;
	}

	public static Object[] getAutoArgs(InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			ValueFactory<String, ? extends Value> valueFactory, Class<?> clazz, ParameterConfig[] parameterConfigs) {
		if (parameterConfigs.length == 0) {
			return new Object[0];
		}

		Object[] args = new Object[parameterConfigs.length];
		for (int i = 0; i < parameterConfigs.length; i++) {
			ParameterConfig parameterConfig = parameterConfigs[i];
			boolean require = !AnnotationUtils.isNullable(parameterConfig, false);
			if (isProerptyType(parameterConfig)) {
				String value = getProperty(propertyFactory, valueFactory, clazz, parameterConfig);
				if (require && StringUtils.isEmpty(value)) {
					return null;
				}

				args[i] = valueFactory.getObject(value, parameterConfig.getGenericType());
			} else {
				if (parameterConfig.getType() == InstanceConfig.class) {
					args[i] = instanceFactory;
					continue;
				}

				if (parameterConfig.getType() == PropertyFactory.class) {
					args[i] = propertyFactory;
					continue;
				}

				String name = getInstanceName(instanceFactory, propertyFactory, clazz, parameterConfig);
				args[i] = name == null ? null : instanceFactory.getInstance(name);
			}
		}
		return args;
	}
}
