package scw.core.instance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.compatible.CompatibleUtils;
import scw.compatible.ServiceLoader;
import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.value.ValueFactory;

@SuppressWarnings("rawtypes")
public final class InstanceUtils {
	private static Logger logger = LoggerUtils.getLogger(InstanceUtils.class);

	private InstanceUtils() {
	};

	public static final ConstructorBuilder EMPTY_INSTANCE_BUILDER = new ConstructorBuilder() {

		public Constructor<?> getConstructor() {
			return null;
		}

		public Object[] getArgs() throws Exception {
			return null;
		}
	};

	public static final ConfigurationScanner CONFIGURATION_SCANNER;

	public static final InstanceFactory INSTANCE_FACTORY = new DefaultInstanceFactory(
			GlobalPropertyFactory.getInstance());

	/**
	 * 不调用构造方法实例化对象
	 */
	public static final NoArgsInstanceFactory NO_ARGS_INSTANCE_FACTORY;

	static {
		ConfigurationScanner configurationScanner = loadService(ConfigurationScanner.class);
		CONFIGURATION_SCANNER = configurationScanner == null ? new ConfigurationScanner() : configurationScanner;

		NoArgsInstanceFactory noArgsInstanceFactory = loadService(NoArgsInstanceFactory.class,
				"scw.core.instance.SunNoArgsInstanceFactory", "scw.core.instance.UnsafeNoArgsInstanceFactory");
		NO_ARGS_INSTANCE_FACTORY = noArgsInstanceFactory;
		if (NO_ARGS_INSTANCE_FACTORY == null) {
			throw new NotSupportedException(NoArgsInstanceFactory.class.getName());
		}
	}

	public static <T> T loadService(Class<? extends T> clazz, String... names) {
		return loadService(clazz, INSTANCE_FACTORY, GlobalPropertyFactory.getInstance(), names);
	}

	public static <T> List<T> loadAllService(Class<? extends T> clazz, String... names) {
		return loadAllService(clazz, INSTANCE_FACTORY, GlobalPropertyFactory.getInstance(), names);
	}

	/**
	 * 使用java spi机制
	 * 
	 * @param clazz
	 * @param names
	 * @return
	 */
	public static <T> T loadService(Class<? extends T> clazz, NoArgsInstanceFactory instanceFactory,
			ValueFactory<String> propertyFactory, String... names) {
		String[] configNames = propertyFactory.getObject(clazz.getName(), String[].class);
		if (!ArrayUtils.isEmpty(configNames)) {
			for (String name : configNames) {
				if (instanceFactory.isInstance(name)) {
					return instanceFactory.getInstance(name);
				}
			}
		}

		T service = null;
		ServiceLoader<? extends T> serviceLoader = CompatibleUtils.getSpi().load(clazz);
		for (T s : serviceLoader) {
			service = s;
			break;
		}

		if (service == null && !ArrayUtils.isEmpty(names)) {
			for (String name : names) {
				if (instanceFactory.isInstance(name)) {
					service = instanceFactory.getInstance(name);
					break;
				}
			}
		}
		return service;
	}

	public static <T> List<T> loadAllService(Class<? extends T> clazz, NoArgsInstanceFactory instanceFactory,
			ValueFactory<String> propertyFactory, String... names) {
		List<T> list = new ArrayList<T>();
		String[] configNames = propertyFactory.getObject(clazz.getName(), String[].class);
		if (!ArrayUtils.isEmpty(configNames)) {
			for (String name : configNames) {
				if (instanceFactory.isInstance(name)) {
					T t = instanceFactory.getInstance(name);
					list.add(t);
				}
			}
		}

		ServiceLoader<? extends T> serviceLoader = CompatibleUtils.getSpi().load(clazz);
		for (T s : serviceLoader) {
			list.add(s);
		}

		if (!ArrayUtils.isEmpty(names)) {
			for (String name : names) {
				if (instanceFactory.isInstance(name)) {
					T t = instanceFactory.getInstance(name);
					list.add(t);
				}
			}
		}
		return list;
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
	public static <T> T newInstance(InstanceFactory instanceFactory, Class<T> type, boolean isPublic,
			Map<String, Object> parameterMap) throws NoSuchMethodException {
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
					return instanceFactory.getInstance(type, constructor.getParameterTypes(), args);
				}
			}
		}
		throw new NoSuchMethodException(type.getName());
	}

	public static <T> Collection<Class<T>> getConfigurationClassList(Class<? extends T> type, ValueFactory<String> propertyFactory,
			Collection<? extends Class> excludeTypes) {
		return getConfigurationClassList(type, excludeTypes,
				Arrays.asList(Constants.SYSTEM_PACKAGE_NAME, getScanAnnotationPackageName(propertyFactory)));
	}

	public static <T> Collection<Class<T>> getConfigurationClassList(Class<? extends T> type, ValueFactory<String> propertyFactory, Class... excludeTypes) {
		return getConfigurationClassList(type, propertyFactory, Arrays.asList(excludeTypes));
	}

	public static <T> Collection<Class<T>> getConfigurationClassList(Class<? extends T> type,
			Collection<? extends Class> excludeTypes, Collection<String> packageNames) {
		return CONFIGURATION_SCANNER.scan(type, excludeTypes, packageNames);
	}

	public static <T> List<T> getConfigurationList(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			ValueFactory<String> propertyFactory, Collection<? extends Class> excludeTypes) {
		return getConfigurationList(type, instanceFactory, excludeTypes,
				Arrays.asList(Constants.SYSTEM_PACKAGE_NAME, getScanAnnotationPackageName(propertyFactory)));
	}

	public static <T> List<T> getConfigurationList(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			Collection<? extends Class> excludeTypes, Collection<String> packageNames) {
		List<T> list = new ArrayList<T>();
		for (Class<? extends T> clazz : getConfigurationClassList(type, excludeTypes, packageNames)) {
			if (!instanceFactory.isInstance(clazz)) {
				logger.debug("factory [{}] not create {} in instance: {}", instanceFactory.getClass(), type, clazz);
				continue;
			}

			list.add(instanceFactory.getInstance(clazz));
		}
		return list;
	}

	public static <T> List<T> getConfigurationList(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			ValueFactory<String> propertyFactory, Class... excludeTypes) {
		return getConfigurationList(type, instanceFactory, propertyFactory, Arrays.asList(excludeTypes));
	}

	public static String getScanAnnotationPackageName(ValueFactory<String> propertyFactory) {
		return propertyFactory.getValue("scw.scan.annotation.package", String.class,
				GlobalPropertyFactory.getInstance().getBasePackageName());
	}

	public static <T> T getConfiguration(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			Collection<? extends Class> excludeTypes, Collection<String> packageNames) {
		for (Class<? extends T> clazz : getConfigurationClassList(type, excludeTypes, packageNames)) {
			if (!instanceFactory.isInstance(clazz)) {
				logger.debug("factory [{}] not create {} in instance: {}", instanceFactory.getClass(), type, clazz);
				continue;
			}

			return instanceFactory.getInstance(clazz);
		}
		return null;
	}

	public static <T> T getConfiguration(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			ValueFactory<String> propertyFactory, Collection<? extends Class> excludeTypes) {
		return getConfiguration(type, instanceFactory, excludeTypes,
				Arrays.asList(Constants.SYSTEM_PACKAGE_NAME, getScanAnnotationPackageName(propertyFactory)));
	}

	public static <T> T getConfiguration(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			ValueFactory<String> propertyFactory, Class... excludeTypes) {
		return getConfiguration(type, instanceFactory, propertyFactory, Arrays.asList(excludeTypes));
	}
}
