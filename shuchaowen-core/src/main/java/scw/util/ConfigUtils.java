package scw.util;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.core.Converter;
import scw.core.PropertyFactory;
import scw.core.StringFormat;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.core.utils.XMLUtils;
import scw.io.resource.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class ConfigUtils {
	private ConfigUtils(){};
	
	private static Logger logger = LoggerUtils.getLogger(ConfigUtils.class);
	private static final String LOG_MESSAGE = "Property {} on target {} set value {}";

	public static <T> T parseObject(Map<String, String> map, Class<T> clz) throws Exception {
		T t = clz.newInstance();
		for (Entry<String, String> entry : map.entrySet()) {
			Field field = ReflectionUtils.getField(clz, entry.getKey(), true);
			if (field == null) {
				continue;
			}

			ReflectionUtils.setFieldValue(clz, field, t,
					StringParse.defaultParse(entry.getValue(), field.getGenericType()));
		}
		return t;
	}

	public static List<Map<String, String>> getDefaultXmlContent(String path, final String rootTag) {
		return ResourceUtils.getResourceOperations().getResource(path,
				new Converter<InputStream, List<Map<String, String>>>() {

					public List<Map<String, String>> convert(InputStream inputStream) {
						return getDefaultXmlContent(inputStream, rootTag);
					}
				});
	}

	public static List<Map<String, String>> getDefaultXmlContent(InputStream inputStream, String rootTag) {
		if (rootTag == null) {
			throw new NullPointerException("rootTag is null");
		}

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			try {
				Document doc = XMLUtils.parse(inputStream);
				Element root = doc.getDocumentElement();
				NodeList nhosts = root.getChildNodes();
				for (int x = 0; x < nhosts.getLength(); x++) {
					Node nRoot = nhosts.item(x);
					if (nRoot.getNodeName().equalsIgnoreCase(rootTag)) {
						list.add(XMLUtils.xmlToMap(nRoot));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static <T> List<T> xmlToList(final Class<T> type, String path) {
		return ResourceUtils.getResourceOperations().getResource(path, new Converter<InputStream, List<T>>() {

			public List<T> convert(InputStream inputStream) {
				return xmlToList(type, inputStream);
			}
		});
	}

	public static <T> List<T> xmlToList(Class<T> type, InputStream inputStream) {
		List<Map<String, String>> list = ConfigUtils.getDefaultXmlContent(inputStream, "config");
		List<T> objList = new ArrayList<T>();
		try {
			for (Map<String, String> map : list) {
				objList.add(ConfigUtils.parseObject(map, type));
			}
			return objList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <K, V> Map<K, V> xmlToMap(final Class<V> valueType, String path) {
		return ResourceUtils.getResourceOperations().getResource(path, new Converter<InputStream, Map<K, V>>() {

			public Map<K, V> convert(InputStream inputStream) {
				return xmlToMap(valueType, inputStream);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> xmlToMap(Class<V> valueType, InputStream inputStream) {
		try {
			Field keyField = null;
			for (Field f : valueType.getDeclaredFields()) {
				if (Modifier.isStatic(f.getModifiers())) {
					continue;
				}

				keyField = f;
				break;
			}

			if (keyField == null) {
				throw new NullPointerException("打不到主键字段");
			}

			List<Map<String, String>> list = ConfigUtils.getDefaultXmlContent(inputStream, "config");
			Map<K, V> map = new HashMap<K, V>();
			for (Map<String, String> tempMap : list) {
				Object obj = ConfigUtils.parseObject(tempMap, valueType);
				keyField.setAccessible(true);
				Object kV = keyField.get(obj);
				keyField.setAccessible(false);
				if (map.containsKey(kV)) {
					throw new NullPointerException("已经存在的key=" + keyField.getName() + ",value=" + kV);
				}
				map.put((K) kV, (V) obj);
			}

			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T setProperties(Object obj, Properties properties, StringFormat stringFormat) {
		T t = null;
		try {
			for (Entry<Object, Object> entry : properties.entrySet()) {
				String key = stringFormat.format(entry.getKey().toString());
				Field field = ReflectionUtils.getField(obj.getClass(), key, true);
				if (field == null) {
					continue;
				}

				String value = entry.getValue() == null ? null : entry.getValue().toString();
				value = stringFormat.format(value);
				ReflectionUtils.setFieldValueAutoType(obj.getClass(), field, obj, value);
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
		loadProperties(instance, ResourceUtils.getResourceOperations().getProperties(propertiesFile), asNameList);
	}

	@SuppressWarnings("rawtypes")
	public static void loadProperties(Object instance, Map properties, Collection<String> asNameList) {
		invokeSetterByProeprties(instance, properties, true, true, asNameList, true);
	}

	public static void invokeSetterByProeprties(Object instance, String propertyPrefix,
			PropertyFactory propertyFactory) {
		invokeSetterByProeprties(instance, propertyPrefix, propertyFactory, true, null, true);
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
			logger.info("Property {} on target {} set value {}", name, instance.getClass().getName(), value);

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

			logger.info(LOG_MESSAGE, name, instance.getClass().getName(), value);

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
}
