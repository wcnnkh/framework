package scw.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.Constants;
import scw.core.Consumer;
import scw.core.PropertyFactory;
import scw.core.StringFormat;
import scw.core.reflect.ReflectUtils;
import scw.io.IOUtils;
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

	public static <T> void loadProperties(T instance, String propertiesFile, Collection<String> asNameList) {
		Properties properties = getProperties(propertiesFile, Constants.DEFAULT_CHARSET_NAME);
		invokeSetterByProeprties(instance, properties, true, true, asNameList, true);
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

	public static Properties getProperties(InputStream inputStream, String charsetName) {
		Properties properties = new Properties();
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(inputStream, charsetName);
			properties.load(isr);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(isr);
		}
		return properties;
	}

	public static Properties getProperties(final String path) {
		final Properties properties = new Properties();
		ResourceUtils.consumterInputStream(path, new Consumer<InputStream>() {

			public void consume(InputStream inputStream) throws Exception {
				if (path.endsWith(".xml")) {
					properties.loadFromXML(inputStream);
				} else {
					properties.load(inputStream);
				}
			}
		});
		return properties;
	}

	public static Properties getProperties(String path, final String charsetName, PropertyFactory propertyFactory) {
		Properties properties = getProperties(path, charsetName);
		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			entry.setValue(FormatUtils.format(value.toString(), propertyFactory, true));
		}
		return properties;
	}

	public static Properties getProperties(String path, final String charsetName) {
		final Method method = ReflectUtils.findMethod(Properties.class, "load", Reader.class);
		if (method == null) {
			LoggerUtils.warn(PropertiesUtils.class, "jdk1.6及以上的版本才支持指定字符集: {}" + path);
			return getProperties(path);
		} else {
			final Properties properties = new Properties();
			ResourceUtils.consumterInputStream(path, new Consumer<InputStream>() {

				public void consume(InputStream inputStream) throws Exception {
					InputStreamReader isr = null;
					try {
						isr = new InputStreamReader(inputStream, charsetName);
						method.invoke(properties, isr);
					} finally {
						IOUtils.close(isr);
					}
				}
			});
			return properties;
		}
	}
}
