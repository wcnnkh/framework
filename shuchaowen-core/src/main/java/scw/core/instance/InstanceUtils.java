package scw.core.instance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.core.Constants;
import scw.core.Copy;
import scw.core.GlobalPropertyFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.CollectionUtils;
import scw.lang.NestedRuntimeException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

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

	public static final ConfigurationScan CONFIGURATION_SCAN = new ConfigurationScan();

	public static final InstanceFactory INSTANCE_FACTORY = new DefaultInstanceFactory(
			GlobalPropertyFactory.getInstance());

	/**
	 * 不调用构造方法实例化对象
	 */
	public static final NoArgsInstanceFactory NO_ARGS_INSTANCE_FACTORY = getSystemConfiguration(
			NoArgsInstanceFactory.class);

	private static final Copy DEFAULT_COPY = new Copy();
	private static final Copy CLONE_COPY = new Copy();
	private static final Copy INVOKER_SETTER_COPY = new Copy();

	static {
		CLONE_COPY.setClone(true);
		INVOKER_SETTER_COPY.setInvokerSetter(true);
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

	public static <T> Collection<Class<T>> getConfigurationClassList(Class<? extends T> type,
			PropertyFactory propertyFactory, Collection<? extends Class> excludeTypes) {
		return getConfigurationClassList(type, propertyFactory, excludeTypes,
				Arrays.asList(Constants.SYSTEM_PACKAGE_NAME, getScanAnnotationPackageName()));
	}

	public static <T> Collection<Class<T>> getConfigurationClassList(Class<? extends T> type,
			PropertyFactory propertyFactory, Class... excludeTypes) {
		return getConfigurationClassList(type, propertyFactory, Arrays.asList(excludeTypes));
	}

	public static <T> Collection<Class<T>> getConfigurationClassList(Class<? extends T> type,
			PropertyFactory propertyFactory, Collection<? extends Class> excludeTypes,
			Collection<String> packageNames) {
		return CONFIGURATION_SCAN.scan(type, propertyFactory, excludeTypes, packageNames);
	}

	public static <T> List<T> getConfigurationList(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Collection<? extends Class> excludeTypes) {
		return getConfigurationList(type, instanceFactory, propertyFactory, excludeTypes,
				Arrays.asList(Constants.SYSTEM_PACKAGE_NAME, getScanAnnotationPackageName()));
	}

	public static <T> List<T> getConfigurationList(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Collection<? extends Class> excludeTypes,
			Collection<String> packageNames) {
		List<T> list = new ArrayList<T>();
		for (Class<T> clazz : getConfigurationClassList(type, propertyFactory, excludeTypes, packageNames)) {
			if (!instanceFactory.isInstance(clazz)) {
				logger.debug("factory [{}] not create {} in instance: {}", instanceFactory.getClass(), type, clazz);
				continue;
			}

			list.add(instanceFactory.getInstance(clazz));
		}
		return list;
	}

	public static <T> List<T> getConfigurationList(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Class... excludeTypes) {
		return getConfigurationList(type, instanceFactory, propertyFactory, Arrays.asList(excludeTypes));
	}

	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue("scw.scan.annotation.package", String.class,
				GlobalPropertyFactory.getInstance().getBasePackageName());
	}

	public static <T> T getConfiguration(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Collection<? extends Class> excludeTypes,
			Collection<String> packageNames) {
		for (Class<T> clazz : getConfigurationClassList(type, propertyFactory, excludeTypes, packageNames)) {
			if (!instanceFactory.isInstance(clazz)) {
				logger.debug("factory [{}] not create {} in instance: {}", instanceFactory.getClass(), type, clazz);
				continue;
			}

			return instanceFactory.getInstance(clazz);
		}
		return null;
	}

	public static <T> T getConfiguration(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Collection<? extends Class> excludeTypes) {
		return getConfiguration(type, instanceFactory, propertyFactory, excludeTypes,
				Arrays.asList(Constants.SYSTEM_PACKAGE_NAME, getScanAnnotationPackageName()));
	}

	public static <T> T getConfiguration(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Class... excludeTypes) {
		return getConfiguration(type, instanceFactory, propertyFactory, Arrays.asList(excludeTypes));
	}

	public static <T> T getSystemConfiguration(Class<? extends T> type, Collection<? extends Class> excludeTypes) {
		return getConfiguration(type, INSTANCE_FACTORY, GlobalPropertyFactory.getInstance(), excludeTypes);
	}

	public static <T> List<T> getSystemConfigurationList(Class<? extends T> type,
			Collection<? extends Class> excludeTypes) {
		return getConfigurationList(type, INSTANCE_FACTORY, GlobalPropertyFactory.getInstance(), excludeTypes);
	}

	public static <T> T getSystemConfiguration(Class<? extends T> type, Class... excludeTypes) {
		return getConfiguration(type, INSTANCE_FACTORY, GlobalPropertyFactory.getInstance(), excludeTypes);
	}

	public static <T> List<T> getSystemConfigurationList(Class<? extends T> type, Class... excludeTypes) {
		return getConfigurationList(type, INSTANCE_FACTORY, GlobalPropertyFactory.getInstance(), excludeTypes);
	}

	public static <T> T clone(T source) {
		try {
			return CLONE_COPY.clone(source);
		} catch (Exception e) {
			throw new NestedRuntimeException("clone error", e);
		}
	}

	public static <T> T copy(Class<? extends T> targetClass, Object source) {
		try {
			return DEFAULT_COPY.copy(targetClass, source);
		} catch (Exception e) {
			throw new NestedRuntimeException("copy error", e);
		}
	}

	public static void copy(Object target, Object source) {
		try {
			INVOKER_SETTER_COPY.copy(target.getClass(), target, source);
		} catch (Exception e) {
			throw new NestedRuntimeException("copy error", e);
		}
	}
}
