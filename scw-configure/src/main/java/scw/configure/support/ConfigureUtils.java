package scw.configure.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.configure.Configure;
import scw.configure.resolver.ResourceResolver;
import scw.configure.resolver.ResourceResolverFactory;
import scw.convert.ConversionService;
import scw.core.Constants;
import scw.core.instance.InstanceUtils;
import scw.core.instance.NoArgsInstanceFactory;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.mapper.Field;
import scw.mapper.Fields;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.xml.XMLUtils;

public final class ConfigureUtils {
	private static final ConfigureFactory CONFIGURE_FACTORY = new ConfigureFactory();
	private static final ResourceResolverFactory RESOURCE_RESOLVER_FACTORY = new ResourceResolverFactory(
			CONFIGURE_FACTORY, Constants.UTF_8_NAME);

	static {
		CONFIGURE_FACTORY.getConfigurations().add(new ResourceConfigure(RESOURCE_RESOLVER_FACTORY, CONFIGURE_FACTORY));
		CONFIGURE_FACTORY.getConfigurations().addAll(
				InstanceUtils.loadAllService(Configure.class));
		
		CONFIGURE_FACTORY.getConversionServices().add(new ResourceConversionService(RESOURCE_RESOLVER_FACTORY));
		CONFIGURE_FACTORY.getConversionServices().addAll(
				InstanceUtils.loadAllService(ConversionService.class));
		
		RESOURCE_RESOLVER_FACTORY.getResourceResolvers().addAll(
				InstanceUtils.loadAllService(ResourceResolver.class));
	}

	private ConfigureUtils() {
	};

	public static ConfigureFactory getConfigureFactory() {
		return CONFIGURE_FACTORY;
	}

	public static ResourceResolverFactory getResourceResolverFactory() {
		return RESOURCE_RESOLVER_FACTORY;
	}

	public static <T> T parseObject(Map<String, String> map, Class<T> clz) {
		T t = InstanceUtils.INSTANCE_FACTORY.getInstance(clz);
		Fields fields = MapperUtils.getMapper().getFields(clz,
				FilterFeature.SUPPORT_SETTER);
		for (Entry<String, String> entry : map.entrySet()) {
			scw.mapper.Field field = fields.find(entry.getKey(), null);
			if (field == null) {
				continue;
			}

			ConfigureUtils.setValue(t, field, entry.getValue());
		}
		return t;
	}

	public static List<Map<String, String>> getDefaultXmlContent(
			Resource resource, final String rootTag) {
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

	public static List<Map<String, String>> getDefaultXmlContent(
			InputStream inputStream, String rootTag) throws IOException {
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
		return xmlToList(type, ResourceUtils.getResourceOperations()
				.getResource(resource));
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

	public static <T> List<T> xmlToList(Class<T> type, InputStream inputStream)
			throws IOException {
		List<Map<String, String>> list = ConfigureUtils.getDefaultXmlContent(
				inputStream, "config");
		List<T> objList = new ArrayList<T>();
		for (Map<String, String> map : list) {
			objList.add(ConfigureUtils.parseObject(map, type));
		}
		return objList;
	}

	public static <K, V> Map<K, V> xmlToMap(final Class<V> valueType,
			String resource) {
		return xmlToMap(valueType, ResourceUtils.getResourceOperations()
				.getResource(resource));
	}

	public static <K, V> Map<K, V> xmlToMap(final Class<V> valueType,
			Resource resource) {
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
	public static <K, V> Map<K, V> xmlToMap(Class<V> valueType,
			InputStream inputStream) throws IOException {
		Fields fields = MapperUtils.getMapper().getFields(valueType,
				FilterFeature.SETTER_IGNORE_STATIC,
				FilterFeature.SUPPORT_GETTER);
		Field keyField = null;
		List<Map<String, String>> list = ConfigureUtils.getDefaultXmlContent(
				inputStream, "config");
		Map<K, V> map = new LinkedHashMap<K, V>();
		for (Map<String, String> tempMap : list) {
			if (keyField == null) {
				for (Entry<String, String> entry : tempMap.entrySet()) {
					keyField = fields.find(entry.getKey(), null);
					if (keyField == null) {
						continue;
					}

					break;
				}
			}

			if (keyField == null) {
				continue;
			}

			Object obj = ConfigureUtils.parseObject(tempMap, valueType);
			Object kV = keyField.getGetter().get(obj);
			if (map.containsKey(kV)) {
				throw new NullPointerException("已经存在的key="
						+ keyField.getGetter().getName() + ",value=" + kV);
			}
			map.put((K) kV, (V) obj);
		}

		return map;
	}

	public static <T> T getBean(NoArgsInstanceFactory instanceFactory, Node node, Class<T> type) {
		T t = null;
		Fields fields = MapperUtils.getMapper().getFields(type, FilterFeature.SUPPORT_SETTER,
				FilterFeature.SETTER_IGNORE_STATIC);
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (XMLUtils.ignoreNode(n)) {
				continue;
			}

			Field field = fields.find(n.getNodeName(), null);
			if (field == null) {
				continue;
			}

			String value = n.getTextContent();
			if (value == null) {
				continue;
			}

			if (t == null) {
				t = instanceFactory.getInstance(type);
			}

			ConfigureUtils.setValue(t, field, value);
		}
		return t;
	}

	public static <T> List<T> getBeanList(NoArgsInstanceFactory instanceFactory, Node rootNode, Class<T> type) {
		if (rootNode == null) {
			return null;
		}

		NodeList nodeList = rootNode.getChildNodes();
		if (nodeList == null) {
			return null;
		}

		List<T> list = new ArrayList<T>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (XMLUtils.ignoreNode(node)) {
				continue;
			}

			T t = getBean(instanceFactory, node, type);
			if (t == null) {
				continue;
			}
			list.add(t);
		}
		return list;
	}
	
	public static void setValue(Object instance, scw.mapper.Field field, Object value){
		MapperUtils.setValue(getConfigureFactory(), instance, field, value);
	}
}
