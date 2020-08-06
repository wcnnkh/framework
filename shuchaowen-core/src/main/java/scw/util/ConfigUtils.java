package scw.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import scw.core.StringFormat;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.value.Value;
import scw.value.property.PropertyFactory;
import scw.xml.XMLUtils;

public final class ConfigUtils {
	private ConfigUtils() {
	};

	private static Logger logger = LoggerUtils.getLogger(ConfigUtils.class);
	private static final String LOG_MESSAGE = "Property {} on target {} set value {}";

	public static <T> T parseObject(Map<String, String> map, Class<T> clz) {
		T t = InstanceUtils.INSTANCE_FACTORY.getInstance(clz);
		for (Entry<String, String> entry : map.entrySet()) {
			scw.mapper.Field field = MapperUtils.getMapper().getField(clz, entry.getKey(), null,
					FilterFeature.SUPPORT_SETTER);
			if (field == null) {
				continue;
			}

			MapperUtils.setStringValue(field, t, entry.getValue());
		}
		return t;
	}

	public static List<Map<String, String>> getDefaultXmlContent(Resource resource, final String rootTag) {
		if (!resource.exists()) {
			return Collections.emptyList();
		}

		InputStream inputStream = null;
		try {
			inputStream = resource.getInputStream();
			return getDefaultXmlContent(inputStream, rootTag);
		} catch (Exception e) {
			throw new RuntimeException(resource.getDescription(), e);
		} finally {
			IOUtils.close(inputStream);
		}
	}

	public static List<Map<String, String>> getDefaultXmlContent(InputStream inputStream, String rootTag)
			throws IOException {
		if (rootTag == null) {
			throw new NullPointerException("rootTag is null");
		}

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Document doc = XMLUtils.parse(inputStream);
		Element root = doc.getDocumentElement();
		NodeList nhosts = root.getChildNodes();
		for (int x = 0; x < nhosts.getLength(); x++) {
			Node nRoot = nhosts.item(x);
			if (nRoot.getNodeName().equalsIgnoreCase(rootTag)) {
				list.add(XMLUtils.xmlToMap(nRoot));
			}
		}
		return list;
	}

	public static <T> List<T> xmlToList(final Class<T> type, String resource) {
		return xmlToList(type, ResourceUtils.getResourceOperations().getResource(resource));
	}

	public static <T> List<T> xmlToList(final Class<T> type, Resource resource) {
		if (!resource.exists()) {
			return Collections.emptyList();
		}

		InputStream inputStream = null;
		try {
			inputStream = resource.getInputStream();
			return xmlToList(type, inputStream);
		} catch (Exception e) {
			throw new RuntimeException(resource.getDescription(), e);
		} finally {
			IOUtils.close(inputStream);
		}
	}

	public static <T> List<T> xmlToList(Class<T> type, InputStream inputStream) throws IOException {
		List<Map<String, String>> list = ConfigUtils.getDefaultXmlContent(inputStream, "config");
		List<T> objList = new ArrayList<T>();
		for (Map<String, String> map : list) {
			objList.add(ConfigUtils.parseObject(map, type));
		}
		return objList;
	}

	public static <K, V> Map<K, V> xmlToMap(final Class<V> valueType, String resource) {
		return xmlToMap(valueType, ResourceUtils.getResourceOperations().getResource(resource));
	}

	public static <K, V> Map<K, V> xmlToMap(final Class<V> valueType, Resource resource) {
		if (!resource.exists()) {
			return Collections.emptyMap();
		}

		InputStream inputStream = null;
		try {
			inputStream = resource.getInputStream();
			return xmlToMap(valueType, inputStream);
		} catch (IOException e) {
			throw new RuntimeException(resource.getDescription(), e);
		} finally {
			IOUtils.close(inputStream);
		}
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> xmlToMap(Class<V> valueType, InputStream inputStream) throws IOException {
		Field keyField = MapperUtils.getMapper().getField(valueType, null, FilterFeature.SETTER_IGNORE_STATIC,
				FilterFeature.SUPPORT_GETTER);
		if (keyField == null) {
			throw new NullPointerException("打不到主键字段");
		}

		List<Map<String, String>> list = ConfigUtils.getDefaultXmlContent(inputStream, "config");
		Map<K, V> map = new HashMap<K, V>();
		for (Map<String, String> tempMap : list) {
			Object obj = ConfigUtils.parseObject(tempMap, valueType);
			Object kV = keyField.getGetter().get(obj);
			if (map.containsKey(kV)) {
				throw new NullPointerException("已经存在的key=" + keyField.getGetter().getName() + ",value=" + kV);
			}
			map.put((K) kV, (V) obj);
		}

		return map;
	}

	public static <T> T setProperties(Object obj, Properties properties, StringFormat stringFormat) {
		T t = null;
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String key = stringFormat.format(entry.getKey().toString());
			scw.mapper.Field fieldContext = MapperUtils.getMapper().getField(obj.getClass(), key, null);
			if (fieldContext == null) {
				continue;
			}

			String value = entry.getValue() == null ? null : entry.getValue().toString();
			value = stringFormat.format(value);
			MapperUtils.setStringValue(fieldContext, obj, value);
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

	public static void loadProperties(Object instance, PropertyFactory propertyFactory, Collection<String> asNameList,
			String propertyPrefix) {
		loadProperties(instance, propertyFactory, asNameList, propertyPrefix, new FieldFilter() {

			public boolean accept(Field field) {
				return isCommonConfigType(field.getSetter().getType());
			}
		});
	}

	public static boolean isCommonConfigType(Class<?> type) {
		if (String.class == type || ClassUtils.isPrimitiveOrWrapper(type) || type == Class.class) {
			return true;
		}

		if (type.isArray()) {
			return isCommonConfigType(type.getComponentType());
		}
		return false;
	}

	public static void loadProperties(Object instance, PropertyFactory propertyFactory, Collection<String> asNameList,
			String propertyPrefix, FieldFilter fieldFilter) {
		List<String> nameList = null;
		if (!CollectionUtils.isEmpty(asNameList)) {
			nameList = new ArrayList<String>(asNameList);
		}

		for (Field field : MapperUtils.getMapper().getFields(instance.getClass(), null, fieldFilter,
				FilterFeature.SETTER)) {
			String name = field.getSetter().getName();
			Value value = propertyFactory.get(StringUtils.isEmpty(propertyPrefix) ? name : (propertyPrefix + name));
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
										.get(StringUtils.isEmpty(propertyPrefix) ? n : (propertyPrefix + n));
								if (value != null) {
									break;
								}
							}
							break;
						}
					}

					if (value != null) {
						iterator.remove();
						break;
					}
				}
			}

			if (value == null) {
				continue;
			}

			logger.info("Property {} on target {} set value {}", name, instance.getClass().getName(), value);
			field.getSetter().set(instance, value.getAsObject(field.getSetter().getGenericType()));
		}
	}

	public static void loadProperties(Object instance, Map<?, ?> properties, Collection<String> asNameList,
			String propertyPrefix) {
		loadProperties(instance, properties, asNameList, propertyPrefix, new FieldFilter() {

			public boolean accept(Field field) {
				return isCommonConfigType(field.getSetter().getType());
			}
		});
	}

	public static void loadProperties(Object instance, Map<?, ?> properties, Collection<String> asNameList,
			String propertyPrefix, FieldFilter fieldFilter) {
		if (properties == null) {
			return;
		}

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

		for (Field field : MapperUtils.getMapper().getFields(instance.getClass(), null, fieldFilter,
				FilterFeature.SETTER)) {
			String name = field.getSetter().getName();
			String value = null;
			if (CollectionUtils.isEmpty(nameList)) {
				value = map.remove(StringUtils.isEmpty(propertyPrefix) ? name : (propertyPrefix + name));
			} else {
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
								String useName = StringUtils.isEmpty(propertyPrefix) ? n : (propertyPrefix + n);
								value = map.get(useName);
								if (value != null) {
									map.remove(useName);
									iterator.remove();
									break;
								}
							}
							break;
						}
					}
				}
			}

			if (value == null) {
				continue;
			}

			logger.info(LOG_MESSAGE, name, instance.getClass().getName(), value);
			MapperUtils.setStringValue(field, instance, value);
		}
	}
}
