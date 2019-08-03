package scw.core.instance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;

import scw.core.exception.NotSupportException;
import scw.core.instance.support.ReflectionInstanceFactory;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.logger.LoggerUtils;

public final class InstanceUtils {
	private InstanceUtils() {
	};

	public static final ReflectionInstanceFactory REFLECTION_INSTANCE_FACTORY = new ReflectionInstanceFactory();

	public static final NoArgsInstanceFactory NO_ARGS_INSTANCE_FACTORY;

	static {
		NoArgsInstanceFactory instanceFactory = REFLECTION_INSTANCE_FACTORY
				.getInstance("scw.core.instance.support.SunNoArgsInstanceFactory");
		if (instanceFactory == null) {
			instanceFactory = REFLECTION_INSTANCE_FACTORY
					.getInstance("scw.core.instance.support.UnsafeNoArgsInstanceFactory");
		}

		if (instanceFactory == null) {
			throw new NotSupportException(
					"Instances that do not call constructors are not supported");
		}

		LoggerUtils.info(InstanceUtils.class,
				"default not call constructors instance factory：{}",
				instanceFactory.getClass().getName());
		NO_ARGS_INSTANCE_FACTORY = instanceFactory;
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String name, boolean invokeConstructor) {
		return (T) (invokeConstructor ? REFLECTION_INSTANCE_FACTORY
				.getInstance(name) : NO_ARGS_INSTANCE_FACTORY.getInstance(name));
	}

	public static <T> T newInstance(Class<T> type, boolean invokeConstructor) {
		return (T) (invokeConstructor ? REFLECTION_INSTANCE_FACTORY
				.getInstance(type) : NO_ARGS_INSTANCE_FACTORY.getInstance(type));
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
	public static <T> T getInstance(String name, Class<?>[] parameterTypes,
			Object... params) {
		return REFLECTION_INSTANCE_FACTORY.getInstance(name, parameterTypes,
				params);
	}

	/**
	 * 执行失败返回空或抛出异常
	 * 
	 * @param type
	 * @param parameterTypes
	 * @param params
	 * @return
	 */
	public static <T> T getInstance(Class<T> type, Class<?>[] parameterTypes,
			Object... params) {
		return REFLECTION_INSTANCE_FACTORY.getInstance(type, parameterTypes,
				params);
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
				return ReflectUtils.getConstructor(type, isPublic)
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
				String[] names = ClassUtils.getParameterName(constructor);
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
}
