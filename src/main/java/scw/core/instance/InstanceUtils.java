package scw.core.instance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import scw.aop.Invoker;
import scw.aop.ReflectInvoker;
import scw.core.PropertyFactory;
import scw.core.SystemPropertyFactory;
import scw.core.instance.support.ReflectionInstanceFactory;
import scw.core.instance.support.ReflectionSingleInstanceFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.FormatUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.lang.NotSupportException;

public final class InstanceUtils {
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

	public static Invoker getInvoker(InstanceFactory instanceFactory, Class<?> clz, Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			return new ReflectInvoker(null, method);
		} else {
			return new ReflectInvoker(instanceFactory.getInstance(clz), method);
		}
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
		return (T) autoNewInstance(ClassUtils.forName(name));
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
			clz = ClassUtils.forName(name);
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
				clz = ClassUtils.forName(name);
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
}
