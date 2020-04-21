package scw.core.instance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import scw.core.parameter.DefaultParameterDescriptorFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.resource.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.comparator.CompareUtils;
import scw.util.value.StringValue;
import scw.util.value.Value;
import scw.util.value.ValueUtils;
import scw.util.value.property.PropertyFactory;

@SuppressWarnings("rawtypes")
public final class InstanceUtils {
	private static Logger logger = LoggerUtils.getConsoleLogger(InstanceUtils.class);

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

	public static final InstanceFactory INSTANCE_FACTORY = new DefaultInstanceFactory(
			GlobalPropertyFactory.getInstance(), new DefaultParameterDescriptorFactory());

	/**
	 * 不调用构造方法实例化对象
	 */
	public static final NoArgsInstanceFactory NO_ARGS_INSTANCE_FACTORY = getSystemConfiguration(
			NoArgsInstanceFactory.class);

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

	private static boolean isProerptyType(ParameterDescriptor parameterConfig) {
		PropertyParameter propertyParameter = parameterConfig.getAnnotatedElement()
				.getAnnotation(PropertyParameter.class);
		if (propertyParameter == null) {
			Class<?> type = parameterConfig.getType();
			return ValueUtils.isCommonType(type) || type.isArray() || Collection.class.isAssignableFrom(type)
					|| Map.class.isAssignableFrom(type);
		} else {
			return propertyParameter.value();
		}
	}

	private static String getDefaultName(Class<?> clazz, ParameterDescriptor parameterConfig) {
		return clazz.getClass().getName() + "." + parameterConfig.getName();
	}

	private static Value getProperty(PropertyFactory propertyFactory, Class<?> clazz,
			ParameterDescriptor parameterConfig) {
		ParameterName parameterName = parameterConfig.getAnnotatedElement().getAnnotation(ParameterName.class);
		Value value = propertyFactory
				.get(parameterName == null ? getDefaultName(clazz, parameterConfig) : parameterName.value());
		if (value == null) {
			DefaultValue defaultValue = parameterConfig.getAnnotatedElement().getAnnotation(DefaultValue.class);
			if (defaultValue != null) {
				value = new StringValue(defaultValue.value());
			}
		}

		if (value != null) {
			ResourceParameter resourceParameter = parameterConfig.getAnnotatedElement()
					.getAnnotation(ResourceParameter.class);
			if (resourceParameter != null) {
				if (!ResourceUtils.getResourceOperations().isExist(value.getAsString())) {
					return null;
				}
			}
		}
		return value;
	}

	private static String getInstanceName(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class<?> clazz, ParameterDescriptor parameterConfig) {
		ParameterName parameterName = parameterConfig.getAnnotatedElement().getAnnotation(ParameterName.class);
		if (parameterName != null && StringUtils.isNotEmpty(parameterName.value())) {
			Value value = propertyFactory.get(parameterName.value());
			if (value == null) {
				return null;
			}

			return instanceFactory.isInstance(value.getAsString()) ? null : value.getAsString();
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

	public static boolean isAuto(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clazz,
			ParameterDescriptor parameterDescriptor, ParameterFactory parameterFactory) {
		boolean require = !AnnotationUtils.isNullable(parameterDescriptor.getAnnotatedElement(), false);
		if (!require) {
			return true;
		}

		if (parameterDescriptor.getType() == clazz) {
			return false;
		}

		if (parameterFactory != null) {
			Object value = parameterFactory.getParameter(parameterDescriptor);
			if (value != null) {
				return true;
			}
		}

		boolean isProperty = isProerptyType(parameterDescriptor);
		// 是否是属性而不是bean
		if (isProperty) {
			Value value = getProperty(propertyFactory, clazz, parameterDescriptor);
			if (value == null) {
				return false;
			}
		} else {
			String name = getInstanceName(instanceFactory, propertyFactory, clazz, parameterDescriptor);
			if (name == null) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAuto(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clazz,
			ParameterDescriptor[] parameterDescriptors, ParameterFactory parameterFactory, Object logFirstParameter) {
		if (parameterDescriptors == null || parameterDescriptors.length == 0) {
			return true;
		}

		for (int i = 0; i < parameterDescriptors.length; i++) {
			ParameterDescriptor parameterDescriptor = parameterDescriptors[i];
			boolean auto = isAuto(instanceFactory, propertyFactory, clazz, parameterDescriptor, parameterFactory);
			if (logger.isDebugEnabled()) {
				logger.debug("{} parameter index {} matching: {}", logFirstParameter, i, auto ? "success" : "fail");
			}

			if (!auto) {
				return false;
			}
		}
		return true;
	}

	public static Object getAutoValue(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class<?> clazz, ParameterDescriptor parameterDescriptor, ParameterFactory parameterFactory) {
		boolean require = !AnnotationUtils.isNullable(parameterDescriptor.getAnnotatedElement(), false);

		if (parameterFactory != null) {
			Object value = parameterFactory.getParameter(parameterDescriptor);
			if (value != null) {
				return value;
			}
		}

		if (isProerptyType(parameterDescriptor)) {
			Value value = getProperty(propertyFactory, clazz, parameterDescriptor);
			if (require && value == null) {
				return null;
			}

			return value.getAsObject(parameterDescriptor.getGenericType());
		} else {
			String name = getInstanceName(instanceFactory, propertyFactory, clazz, parameterDescriptor);
			return name == null ? null : instanceFactory.getInstance(name);
		}
	}

	public static Object[] getAutoArgs(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class<?> clazz, ParameterDescriptor[] parameterDescriptors, ParameterFactory parameterFactory) {
		if (parameterDescriptors == null || parameterDescriptors.length == 0) {
			return new Object[0];
		}

		Object[] args = new Object[parameterDescriptors.length];
		for (int i = 0; i < parameterDescriptors.length; i++) {
			args[i] = getAutoValue(instanceFactory, propertyFactory, clazz, parameterDescriptors[i], parameterFactory);

		}
		return args;
	}

	private static Set<Class<?>> getConfigurationClassListInternal(Class<?> type, String packageName) {
		Set<Class<?>> list = new HashSet<Class<?>>();
		for (Class<?> clazz : ClassUtils.getClassSet(packageName)) {
			if (clazz == type) {
				continue;
			}

			if (!ClassUtils.isAssignable(type, clazz)) {
				continue;
			}

			Configuration configuration = clazz.getAnnotation(Configuration.class);
			if (configuration == null) {
				continue;
			}

			if (configuration.value().length != 0) {
				Collection<Class<?>> values = Arrays.asList(configuration.value());
				if (configuration.assignableValue()) {
					if (!ClassUtils.isAssignable(values, type)) {
						continue;
					}
				} else {
					if (!values.contains(type)) {
						continue;
					}
				}
			}

			if (!ClassUtils.isPresent(clazz.getName())) {
				logger.debug("not support class: {}", clazz.getName());
				continue;
			}

			list.add(clazz);
		}
		return list;
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

	@SuppressWarnings({ "unchecked" })
	public static <T> Collection<Class<T>> getConfigurationClassList(Class<? extends T> type,
			PropertyFactory propertyFactory, Collection<? extends Class> excludeTypes,
			Collection<? extends String> packageNames) {
		Set<Class<T>> set = new LinkedHashSet<Class<T>>();
		for (String packageName : packageNames) {
			for (Class<?> clazz : getConfigurationClassListInternal(type, packageName)) {
				Configuration configuration = clazz.getAnnotation(Configuration.class);
				if (configuration == null) {
					continue;
				}

				if (ClassUtils.isAssignable(excludeTypes, clazz)) {
					continue;
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

		set.clear();
		String[] configNames = propertyFactory.getObject(type.getName(), String[].class);
		if (!ArrayUtils.isEmpty(configNames)) {
			for (String name : configNames) {
				Class<?> clazz = ClassUtils.forNameNullable(name);
				if (clazz == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("not create class by name: {}", name);
					}
					continue;
				}

				if (ClassUtils.isAssignable(type, clazz)) {
					continue;
				}

				if (ClassUtils.isAssignable(excludeTypes, clazz)) {
					continue;
				}
				set.add((Class<T>) clazz);
			}
		}

		for (Class<T> clazz : list) {
			if (set.contains(clazz)) {
				continue;
			}

			set.add(clazz);
		}
		return set;
	}

	public static <T> List<T> getConfigurationList(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Collection<? extends Class> excludeTypes) {
		return getConfigurationList(type, instanceFactory, propertyFactory, excludeTypes,
				Arrays.asList(Constants.SYSTEM_PACKAGE_NAME, getScanAnnotationPackageName()));
	}

	public static <T> List<T> getConfigurationList(Class<? extends T> type, NoArgsInstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Collection<? extends Class> excludeTypes,
			Collection<? extends String> packageNames) {
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
			Collection<? extends String> packageNames) {
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
}
