package scw.beans.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanMethod;
import scw.beans.BeansException;
import scw.beans.EParameterType;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.util.value.property.PropertyFactory;

public final class XmlBeanUtils {
	private XmlBeanUtils() {
	};

	public static XmlBeanParameter parseBeanParameter(Node node)
			throws ClassNotFoundException {
		String name = XMLUtils.getNodeAttributeValue(node, "name");
		String ref = XMLUtils.getNodeAttributeValue(node, "ref");
		String value = XMLUtils.getNodeAttributeValue(node, "value");
		String type = XMLUtils.getNodeAttributeValue(node, "type");
		String property = XMLUtils.getNodeAttributeValue(node, "property");

		Class<?> typeClass = StringUtils.isNull(type) ? null : ClassUtils
				.forName(type);
		if (!StringUtils.isEmpty(ref)) {
			return new XmlBeanParameter(EParameterType.ref, typeClass, name,
					ref, node);
		} else if (!StringUtils.isEmpty(property)) {
			return new XmlBeanParameter(EParameterType.property, typeClass,
					name, property, node);
		} else {
			if (StringUtils.isEmpty(value)) {
				value = node.getNodeValue();
			}
			return new XmlBeanParameter(EParameterType.value, typeClass, name,
					value, node);
		}
	}

	public static boolean isSingleton(Node node) {
		return XMLUtils.getBooleanValue(node, "singleton", true);
	}

	public static String[] getNames(Node node) {
		String name = XMLUtils.getNodeAttributeValue(node, "name");
		return StringUtils.isEmpty(name) ? null : StringUtils.commonSplit(name);
	}

	public static boolean getBooleanValue(PropertyFactory propertyFactory,
			Node node, String name, boolean defaultValue) {
		String value = XMLUtils.getNodeAttributeValue(propertyFactory, node,
				name);
		return StringUtils.isEmpty(value) ? defaultValue : Boolean
				.parseBoolean(value);
	}

	public static int getIntegerValue(PropertyFactory propertyFactory,
			Node node, String name, int defaultValue) {
		String value = XMLUtils.getNodeAttributeValue(propertyFactory, node,
				name);
		return StringUtils.isEmpty(value) ? defaultValue : Integer
				.parseInt(value);
	}

	public static List<XmlBeanParameter> parseBeanParameterList(Node node)
			throws ClassNotFoundException {
		List<XmlBeanParameter> xmlBeanParameters = new ArrayList<XmlBeanParameter>();
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nRoot = nodeList.item(i);
			if (nRoot.getNodeName().equals("parameter")) {
				if (nRoot.getAttributes() == null) {
					continue;
				}

				xmlBeanParameters.add(parseBeanParameter(nRoot));
			}
		}
		return xmlBeanParameters;
	}

	public static NodeList getRootNodeList(String config) {
		return XMLUtils.getChildNodes(getRootNode(config), true);
	}

	public static Node getRootNode(String config) {
		try {
			Node root = XMLUtils.getRootElement(config);
			if (!"beans".equals(root.getNodeName())) {
				throw new BeansException("root tag name error ["
						+ root.getNodeName() + "]");
			}
			return root;
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	public static String getPackageName(PropertyFactory propertyFactory,
			Node node) {
		return XMLUtils.getNodeAttributeValue(propertyFactory, node, "package");
	}

	public static String getVersion(PropertyFactory propertyFactory, Node node) {
		return XMLUtils.getNodeAttributeValue(propertyFactory, node, "version");
	}

	public static String getAddress(PropertyFactory propertyFactory, Node node) {
		return XMLUtils.getRequireNodeAttributeValue(propertyFactory, node,
				"address");
	}

	public static String getRef(PropertyFactory propertyFactory, Node node) {
		return XMLUtils.getRequireNodeAttributeValue(propertyFactory, node,
				"ref");
	}

	public static String getCharsetName(PropertyFactory propertyFactory,
			Node node, String defaultValue) {
		String charsetName = XMLUtils.getNodeAttributeValue(propertyFactory,
				node, "charsetName");
		return StringUtils.isNull(charsetName) ? defaultValue : charsetName;
	}

	public static String getCharsetName(Node node, String defaultValue) {
		String charsetName = XMLUtils
				.getNodeAttributeValue(node, "charsetName");
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
	public static List<BeanMethod> getBeanMethodList(Class<?> clz,
			NodeList nodeList, String tagName) throws Exception {
		List<BeanMethod> list = new ArrayList<BeanMethod>();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if (tagName.equalsIgnoreCase(n.getNodeName())) {
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(
						clz, n);
				list.add(xmlBeanMethodInfo);
			}
		}
		return list;
	}

	public static List<BeanMethod> getInitMethodList(Class<?> clz,
			NodeList nodeList) throws Exception {
		return XmlBeanUtils.getBeanMethodList(clz, nodeList, "init");
	}

	public static List<BeanMethod> getDestroyMethodList(Class<?> clz,
			NodeList nodeList) throws Exception {
		return XmlBeanUtils.getBeanMethodList(clz, nodeList,
				"destroy");
	}

	public static XmlBeanParameter[] getConstructorParameters(NodeList nodeList)
			throws Exception {
		List<XmlBeanParameter> constructorParameterList = new ArrayList<XmlBeanParameter>();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if ("constructor".equalsIgnoreCase(n.getNodeName())) {// Constructor
				List<XmlBeanParameter> list = parseBeanParameterList(n);
				if (list != null) {
					constructorParameterList.addAll(list);
				}
			}
		}

		return CollectionUtils.isEmpty(constructorParameterList) ? null
				: constructorParameterList
						.toArray(new XmlBeanParameter[constructorParameterList
								.size()]);
	}

	public static XmlBeanParameter[] getBeanProperties(NodeList nodeList)
			throws Exception {
		List<XmlBeanParameter> propertiesList = new ArrayList<XmlBeanParameter>();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if ("properties".equalsIgnoreCase(n.getNodeName())) {// Properties
				List<XmlBeanParameter> list = XmlBeanUtils
						.parseBeanParameterList(n);
				if (list != null) {
					propertiesList.addAll(list);
				}
			}
		}

		return CollectionUtils.isEmpty(propertiesList) ? null : propertiesList
				.toArray(new XmlBeanParameter[propertiesList.size()]);
	}

	@SuppressWarnings("unchecked")
	public static Collection<String> getFilters(Node node) {
		String filters = XMLUtils.getNodeAttributeValue(node, "filters");
		if (StringUtils.isEmpty(filters)) {
			return Collections.EMPTY_LIST;
		}

		return Arrays.asList(StringUtils.commonSplit(filters));
	}

	public static String getClassName(Node node) {
		return XMLUtils.getRequireNodeAttributeValue(node, "class");
	}

	public static String getId(Node node) {
		String id = XMLUtils.getNodeAttributeValue(node, "id");
		return StringUtils.isEmpty(id) ? getClassName(node) : id;
	}

	public static Class<?> getClass(Node node) {
		try {
			return ClassUtils.forName(getClassName(node));
		} catch (ClassNotFoundException e) {
			throw new BeansException(e);
		}
	}

	public static TimeUnit getTimeUnit(Node node) {
		String format = XMLUtils.getNodeAttributeValue(node, "unit");
		TimeUnit timeUnit = TimeUnit.MINUTES;
		if (StringUtils.isNotEmpty(format)) {
			timeUnit = TimeUnit.valueOf(format.toUpperCase());
		}
		return timeUnit;
	}
}
