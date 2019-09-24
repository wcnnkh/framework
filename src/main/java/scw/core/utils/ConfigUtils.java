package scw.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.core.Converter;
import scw.core.reflect.ReflectUtils;
import scw.io.IOUtils;

public final class ConfigUtils {
	private ConfigUtils() {
	};

	public static <T> T parseObject(Map<String, String> map, Class<T> clz)
			throws Exception {
		T t = clz.newInstance();
		for (Entry<String, String> entry : map.entrySet()) {
			Field field = ReflectUtils.getField(clz, entry.getKey(), true);
			if (field == null) {
				continue;
			}

			ReflectUtils.setFieldValue(clz, field, t, StringParse.defaultParse(entry.getValue(), field.getGenericType()));
		}
		return t;
	}

	public static List<Map<String, String>> getDefaultXmlContent(String path,
			final String rootTag) {
		return ResourceUtils.getAndConvert(path,
				new Converter<InputStream, List<Map<String, String>>>() {

					public List<Map<String, String>> convert(
							InputStream inputStream) {
						return getDefaultXmlContent(inputStream, rootTag);
					}
				});
	}

	public static List<Map<String, String>> getDefaultXmlContent(
			InputStream inputStream, String rootTag) {
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
		return ResourceUtils.getAndConvert(path,
				new Converter<InputStream, List<T>>() {

					public List<T> convert(InputStream inputStream) {
						return xmlToList(type, inputStream);
					}
				});
	}

	public static <T> List<T> xmlToList(Class<T> type, InputStream inputStream) {
		List<Map<String, String>> list = ConfigUtils.getDefaultXmlContent(
				inputStream, "config");
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

	public static <K, V> Map<K, V> xmlToMap(final Class<V> valueType,
			String path) {
		return ResourceUtils.getAndConvert(path,
				new Converter<InputStream, Map<K, V>>() {

					public Map<K, V> convert(InputStream inputStream) {
						return xmlToMap(valueType, inputStream);
					}
				});
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> xmlToMap(Class<V> valueType,
			InputStream inputStream) {
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

			List<Map<String, String>> list = ConfigUtils.getDefaultXmlContent(
					inputStream, "config");
			Map<K, V> map = new HashMap<K, V>();
			for (Map<String, String> tempMap : list) {
				Object obj = ConfigUtils.parseObject(tempMap, valueType);
				keyField.setAccessible(true);
				Object kV = keyField.get(obj);
				keyField.setAccessible(false);
				if (map.containsKey(kV)) {
					throw new NullPointerException("已经存在的key="
							+ keyField.getName() + ",value=" + kV);
				}
				map.put((K) kV, (V) obj);
			}

			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getFileContentLineList(String path,
			final String charsetName) {
		return ResourceUtils.getAndConvert(path,
				new Converter<InputStream, List<String>>() {

					public List<String> convert(InputStream inputStream) {
						try {
							return IOUtils.readLines(inputStream, charsetName);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}
				});
	}

	public static String getFileContent(String path, final String charsetName) {
		return ResourceUtils.getAndConvert(path,
				new Converter<InputStream, String>() {

					public String convert(InputStream inputStream) {
						return IOUtils.readContent(inputStream, charsetName);
					}
				});
	}
}
