package scw.beans.xml;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanMethod;
import scw.beans.EParameterType;
import scw.beans.property.PropertiesFactory;
import scw.core.exception.BeansException;
import scw.core.reflect.ReflectUtils;
import scw.core.reflect.SetterMapper;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public final class XmlBeanUtils {
	public static final String PARAMETER_TAG_NAME = "parameter";

	private XmlBeanUtils() {
	};

	public static String formatNodeValue(final PropertiesFactory propertiesFactory, Node node, String value) {
		XmlValue xmlValue = new XmlValue(value, node);
		return xmlValue.formatValue(propertiesFactory);
	}

	public static String getNodeAttributeValue(final PropertiesFactory propertiesFactory, Node node, String name) {
		String value = XMLUtils.getNodeAttributeValue(node, name);
		if (value == null || value.length() == 0) {
			return value;
		}

		return formatNodeValue(propertiesFactory, node, value);
	}

	public static String getRequireNodeAttributeValue(PropertiesFactory propertiesFactory, Node node, String name) {
		String value = getNodeAttributeValue(propertiesFactory, node, name);
		if (StringUtils.isNull(value)) {
			throw new BeansException("not found attribute " + name);
		}
		return value;
	}

	public static XmlBeanParameter parseBeanParameter(Node node) throws ClassNotFoundException {
		String name = XMLUtils.getNodeAttributeValue(node, "name");
		String ref = XMLUtils.getNodeAttributeValue(node, "ref");
		String value = XMLUtils.getNodeAttributeValue(node, "value");
		String type = XMLUtils.getNodeAttributeValue(node, "type");
		String property = XMLUtils.getNodeAttributeValue(node, "property");

		Class<?> typeClass = StringUtils.isNull(type) ? null : ClassUtils.forName(type);
		if (!StringUtils.isNull(ref)) {
			return new XmlBeanParameter(EParameterType.ref, typeClass, name, ref, node);
		} else if (!StringUtils.isNull(property)) {
			return new XmlBeanParameter(EParameterType.property, typeClass, name, property, node);
		} else {
			if (StringUtils.isNull(value)) {
				value = node.getNodeValue();
			}
			return new XmlBeanParameter(EParameterType.value, typeClass, name, value, node);
		}
	}

	public static boolean isSingleton(Node node) {
		return XMLUtils.getBooleanValue(node, "singleton", true);
	}

	public static String[] getNames(Node node) {
		String name = XMLUtils.getNodeAttributeValue(node, "name");
		return StringUtils.isNull(name) ? null : StringUtils.commonSplit(name);
	}

	public static String getNodeValue(PropertiesFactory propertiesFactory, Node node, String name) {
		String value = XMLUtils.getNodeAttributeValue(node, name);
		if (StringUtils.isNull(value)) {
			value = node.getNodeValue();
		}

		return formatNodeValue(propertiesFactory, node, value);
	}

	public static boolean getBooleanValue(PropertiesFactory propertiesFactory, Node node, String name,
			boolean defaultValue) {
		String value = getNodeAttributeValue(propertiesFactory, node, name);
		return StringUtils.isNull(value) ? defaultValue : Boolean.parseBoolean(value);
	}

	public static int getIntegerValue(PropertiesFactory propertiesFactory, Node node, String name, int defaultValue) {
		String value = getNodeAttributeValue(propertiesFactory, node, name);
		return StringUtils.isNull(value) ? defaultValue : Integer.parseInt(value);
	}

	public static List<XmlBeanParameter> parseBeanParameterList(Node node) throws ClassNotFoundException {
		List<XmlBeanParameter> xmlBeanParameters = new ArrayList<XmlBeanParameter>();
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nRoot = nodeList.item(i);
			if (nRoot.getNodeName().equals(PARAMETER_TAG_NAME)) {
				if (nRoot.getAttributes() == null) {
					continue;
				}

				xmlBeanParameters.add(parseBeanParameter(nRoot));
			}
		}
		return xmlBeanParameters;
	}

	public static Node getRootNode(String config) {
		try {
			Node root = XMLUtils.getRootElement(config);
			if (!"beans".equals(root.getNodeName())) {
				throw new BeansException("root tag name error [" + root.getNodeName() + "]");
			}
			return root;
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	public static String getPackageName(PropertiesFactory propertiesFactory, Node node) {
		return getNodeAttributeValue(propertiesFactory, node, "package");
	}

	public static String getVersion(PropertiesFactory propertiesFactory, Node node) {
		return getNodeAttributeValue(propertiesFactory, node, "version");
	}

	public static String getAddress(PropertiesFactory propertiesFactory, Node node) {
		return getRequireNodeAttributeValue(propertiesFactory, node, "address");
	}

	public static String getCharsetName(PropertiesFactory propertiesFactory, Node node, String defaultValue) {
		String charsetName = getNodeAttributeValue(propertiesFactory, node, "charset");
		return StringUtils.isNull(charsetName) ? defaultValue : charsetName;
	}

	public static String getCharsetName(Node node, String defaultValue) {
		String charsetName = XMLUtils.getNodeAttributeValue(node, "charset");
		return StringUtils.isNull(charsetName) ? defaultValue : charsetName;
	}

	/**
	 * 获取指定的方法
	 * 
	 * @param clz
	 * @param nodeList
	 * @param tagName
	 * @return
	 * @throws Exception
	 */
	public static List<BeanMethod> getBeanMethodList(Class<?> clz, NodeList nodeList, String tagName) throws Exception {
		List<BeanMethod> list = new ArrayList<BeanMethod>();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if (tagName.equalsIgnoreCase(n.getNodeName())) {
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(clz, n);
				list.add(xmlBeanMethodInfo);
			}
		}
		return list;
	}

	public static <T> T newInstanceLoadAttributeBySetter(Class<T> type, final PropertiesFactory propertiesFactory,
			Node node, final SetterMapper<String> mapper) {
		Map<String, Node> map = XMLUtils.attributeAsMap(node);
		try {
			T t = ClassUtils.newInstance(type);
			ReflectUtils.setProperties(type, t, map, true, new SetterMapper<Node>() {

				public Object mapper(Object bean, Method method, String name, Node value, Class<?> type)
						throws Throwable {
					XmlValue xmlValue = new XmlValue(value.getNodeValue(), value);
					String v = xmlValue.formatValue(propertiesFactory);
					if (StringUtils.isEmpty(v)) {
						return null;
					}

					if (Class.class.isAssignableFrom(type)) {
						return Class.forName(v);
					}

					return mapper.mapper(bean, method, name, v, type);
				}
			});
			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
