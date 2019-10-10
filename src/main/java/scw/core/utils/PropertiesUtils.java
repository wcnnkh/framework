package scw.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.PropertyFactory;
import scw.core.StringFormat;
import scw.core.SystemPropertyFactory;
import scw.core.reflect.ReflectUtils;
import scw.logger.LoggerUtils;

public final class PropertiesUtils {
	private PropertiesUtils() {
	};

	public static <T> T setProperties(Object obj, Properties properties, StringFormat stringFormat) {
		T t = null;
		try {
			for (Entry<Object, Object> entry : properties.entrySet()) {
				String key = stringFormat.format(entry.getKey().toString());
				Field field = ReflectUtils.getField(obj.getClass(), key, true);
				if (field == null) {
					continue;
				}

				String value = entry.getValue() == null ? null : entry.getValue().toString();
				value = stringFormat.format(value);
				ReflectUtils.setFieldValueAutoType(obj.getClass(), field, obj, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			t = null;
		}
		return t;
	}

	public static String getProperty(Properties properties, String... key) {
		if (key.length == 0) {
			return null;
		}

		for (String k : key) {
			Object v = properties.getProperty(k);
			if (v != null) {
				return v.toString();
			}
		}
		return null;
	}

	public static String getProperty(Properties properties, Object defaultValue, String... key) {
		String v = getProperty(properties, key);
		return v == null ? (defaultValue == null ? null : defaultValue.toString()) : v;
	}

	public static void loadProperties(Object instance, String propertiesFile, Collection<String> asNameList) {
		loadProperties(instance, getProperties(propertiesFile), asNameList);
	}

	@SuppressWarnings("rawtypes")
	public static void loadProperties(Object instance, Map properties, Collection<String> asNameList) {
		invokeSetterByProeprties(instance, properties, true, true, asNameList, true);
	}

	public static void loadProperties(Object instance, String propertyPrefix, PropertyFactory propertyFactory,
			Collection<String> asNameList) {
		invokeSetterByProeprties(instance, propertyPrefix, propertyFactory, true, asNameList, true);
	}

	public static void invokeSetterByProeprties(Object instance, String propertyPrefix, PropertyFactory propertyFactory,
			boolean invokePublic, Collection<String> asNameList, boolean findAndRemove) {
		List<String> nameList = null;
		if (!CollectionUtils.isEmpty(asNameList)) {
			nameList = new ArrayList<String>(asNameList);
		}

		for (Method method : invokePublic ? instance.getClass().getMethods()
				: instance.getClass().getDeclaredMethods()) {
			Type[] parameterTypes = method.getGenericParameterTypes();
			if (!(parameterTypes.length == 1 && method.getName().startsWith("set"))) {
				continue;
			}

			Type parameterType = parameterTypes[0];
			if (!(TypeUtils.isPrimitiveOrWrapper(parameterType) || parameterType == String.class)) {
				continue;
			}

			String name = method.getName().substring(3);
			if (name.length() == 1) {
				name = name.toLowerCase();
			} else {
				name = name.substring(0, 1).toLowerCase() + name.substring(1);
			}

			String value = propertyFactory
					.getProperty(StringUtils.isEmpty(propertyPrefix) ? name : (propertyPrefix + name));
			if (value == null && nameList != null) {
				Iterator<String> iterator = nameList.iterator();
				while (iterator.hasNext()) {
					String asNames = iterator.next();
					if (StringUtils.isEmpty(asNames)) {
						iterator.remove();
						continue;
					}

					String[] names = StringUtils.commonSplit(asNames);
					for (String asName : names) {
						if (asName.equals(name)) {
							for (String n : names) {
								value = propertyFactory
										.getProperty(StringUtils.isEmpty(propertyPrefix) ? n : (propertyPrefix + n));
								if (value != null) {
									break;
								}
							}
							break;
						}
					}

					if (value != null) {
						if (findAndRemove) {
							iterator.remove();
						}
						break;
					}
				}
			}

			if (value == null) {
				continue;
			}

			method.setAccessible(false);

			LoggerUtils.info(PropertiesUtils.class, "Property {} on target {} set value {}", name,
					instance.getClass().getName(), value);

			try {
				method.invoke(instance, StringParse.defaultParse(value, parameterType));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 调用对象的set方法
	 * 
	 * @param type
	 * @param instance
	 * @param properties
	 * @param propertieGetAndRemove
	 * @param invokePublic
	 * @param asNameList
	 *            别名
	 * @param findAndRemove
	 * @param log
	 */
	public static void invokeSetterByProeprties(Object instance, Map<?, ?> properties, boolean propertieGetAndRemove,
			boolean invokePublic, Collection<String> asNameList, boolean findAndRemove) {
		List<String> nameList = null;
		if (!CollectionUtils.isEmpty(asNameList)) {
			nameList = new ArrayList<String>(asNameList);
		}

		Map<String, String> map = new HashMap<String, String>();
		for (Entry<?, ?> entry : properties.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (key == null || value == null) {
				continue;
			}
			map.put(key.toString(), value.toString());
		}

		for (Method method : invokePublic ? instance.getClass().getMethods()
				: instance.getClass().getDeclaredMethods()) {
			Type[] parameterTypes = method.getGenericParameterTypes();
			if (!(parameterTypes.length == 1 && method.getName().startsWith("set"))) {
				continue;
			}

			Type parameterType = parameterTypes[0];
			if (!(TypeUtils.isPrimitiveOrWrapper(parameterType) || parameterType == String.class)) {
				continue;
			}

			String name = method.getName().substring(3);
			if (name.length() == 1) {
				name = name.toLowerCase();
			} else {
				name = name.substring(0, 1).toLowerCase() + name.substring(1);
			}

			String value = map.get(name);
			if (value == null && nameList != null) {
				Iterator<String> iterator = nameList.iterator();
				while (iterator.hasNext()) {
					String asNames = iterator.next();
					if (StringUtils.isEmpty(asNames)) {
						iterator.remove();
						continue;
					}

					String[] names = StringUtils.commonSplit(asNames);
					for (String asName : names) {
						if (asName.equals(name)) {
							for (String n : names) {
								value = map.get(n);
								if (value != null) {
									if (propertieGetAndRemove) {
										map.remove(n);
									}
									break;
								}
							}
							break;
						}
					}

					if (value != null) {
						if (findAndRemove) {
							iterator.remove();
						}
						break;
					}
				}
			}

			if (value == null) {
				continue;
			}

			method.setAccessible(false);

			LoggerUtils.info(PropertiesUtils.class, "Property {} on target {} set value {}", name,
					instance.getClass().getName(), value);

			try {
				method.invoke(instance, StringParse.defaultParse(value, parameterType));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static Map<String, String> getProperties(Properties properties) {
		if (CollectionUtils.isEmpty(properties)) {
			return null;
		}

		Map<String, String> map = new LinkedHashMap<String, String>(properties.size(), 1);
		for (Entry<?, ?> entry : properties.entrySet()) {
			Object key = entry.getKey();
			if (key == null) {
				continue;
			}

			Object value = entry.getValue();
			map.put(key.toString(), value == null ? null : value.toString());
		}
		return map;
	}

	public static Properties getProperties(final String resource, final String charsetName,
			PropertyFactory propertyFactory) {
		List<String> resourceNameList = ResourceUtils.getResourceNameList(resource);
		ListIterator<String> iterator = resourceNameList.listIterator(resourceNameList.size());
		Properties properties = new Properties();
		while (iterator.hasPrevious()) {
			final String name = iterator.previous();
			if (ResourceUtils.isExist(name, false)) {
				ResourceUtils.consumterInputStream(name, new LoadProperties(properties, name, charsetName), false);
			}
		}

		if (propertyFactory == null) {
			return properties;
		}

		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			entry.setValue(FormatUtils.format(value.toString(), propertyFactory, true));
		}
		return properties;
	}

	public static Properties getProperties(final String path) {
		return getProperties(path, (String) null);
	}

	public static Properties getProperties(final String path, final String charsetName) {
		return getProperties(path, charsetName, SystemPropertyFactory.INSTANCE);
	}

	public static Properties getProperties(final String path, PropertyFactory propertyFactory) {
		return getProperties(path, null, propertyFactory);
	}
}
